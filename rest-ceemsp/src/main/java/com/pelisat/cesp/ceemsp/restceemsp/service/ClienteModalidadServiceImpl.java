package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.ClienteModalidad;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.ClienteModalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.ClienteRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteModalidadServiceImpl implements ClienteModalidadService {

    private final ClienteModalidadRepository clienteModalidadRepository;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final ClienteRepository clienteRepository;
    private final Logger logger = LoggerFactory.getLogger(ClienteModalidadService.class);
    private final EmpresaModalidadService empresaModalidadService;

    @Autowired
    public ClienteModalidadServiceImpl(ClienteModalidadRepository clienteModalidadRepository, UsuarioService usuarioService,
                                       DaoHelper<CommonModel> daoHelper, ClienteRepository clienteRepository,
                                       EmpresaModalidadService empresaModalidadService) {
        this.clienteModalidadRepository = clienteModalidadRepository;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.clienteRepository = clienteRepository;
        this.empresaModalidadService = empresaModalidadService;
    }

    @Override
    public List<ClienteModalidadDto> obtenerModalidadesPorCliente(String uuid, String clienteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("Alguno de los parametros viene como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo todas las modalidades para el cliente [{}]", clienteUuid);
        Cliente cliente = clienteRepository.findByUuid(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<ClienteModalidad> clienteModalidades = clienteModalidadRepository.getAllByClienteAndEliminadoFalse(cliente.getId());

        return clienteModalidades.stream().map(c -> {
            ClienteModalidadDto cmd = new ClienteModalidadDto();
            cmd.setId(c.getId());
            cmd.setUuid(c.getUuid());
            cmd.setModalidad(empresaModalidadService.obtenerEmpresaModalidadPorId(c.getEmpresaModalidad()));
            return cmd;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteModalidadDto guardarModalidadCliente(String uuid, String clienteUuid, String username, ClienteModalidadDto clienteModalidadDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(username) || clienteModalidadDto == null) {
            logger.warn("Alguno de los datos viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Guardando la modalidad en el cliente con uuid [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);
        if(cliente == null) {
            logger.warn("El cliente no se encuentra en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        ClienteModalidad clienteModalidad = new ClienteModalidad();
        clienteModalidad.setUuid(RandomStringUtils.randomAlphanumeric(12));
        clienteModalidad.setCliente(cliente.getId());
        clienteModalidad.setEmpresaModalidad(clienteModalidadDto.getModalidad().getId());
        daoHelper.fulfillAuditorFields(true, clienteModalidad, usuario.getId());

        clienteModalidadRepository.save(clienteModalidad);

        if(!cliente.isModalidadCapturada()) {
            cliente.setModalidadCapturada(true);
            daoHelper.fulfillAuditorFields(false, cliente, usuario.getId());
            clienteRepository.save(cliente);
        }

        return clienteModalidadDto;
    }

    @Override
    @Transactional
    public ClienteModalidadDto modificarModalidadCliente(String uuid, String clienteUuid, String modalidadUuid, String username, ClienteModalidadDto clienteModalidadDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(modalidadUuid) || StringUtils.isBlank(username) || clienteModalidadDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Modificando la modalidad con el uuid [{}]", modalidadUuid);

        ClienteModalidad clienteModalidad = clienteModalidadRepository.getByUuidAndEliminadoFalse(modalidadUuid);
        if(clienteModalidad == null) {
            logger.warn("La modalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        clienteModalidad.setEmpresaModalidad(clienteModalidadDto.getModalidad().getId());
        daoHelper.fulfillAuditorFields(false, clienteModalidad, usuarioDto.getId());

        clienteModalidadRepository.save(clienteModalidad);
        return clienteModalidadDto;
    }

    @Override
    @Transactional
    public ClienteModalidadDto eliminarModalidad(String uuid, String clienteUuid, String modalidadUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(modalidadUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la modalidad con el cliente [{}]", modalidadUuid);

        ClienteModalidad clienteModalidad = clienteModalidadRepository.getByUuidAndEliminadoFalse(modalidadUuid);

        if(clienteModalidad == null) {
            logger.warn("La modalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        clienteModalidad.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, clienteModalidad, usuarioDto.getId());

        clienteModalidadRepository.save(clienteModalidad);
        return null;
    }
}
