package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteAsignacionPersonalDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.ClienteAsignacionPersonal;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.ClienteAsignacionPersonalRepository;
import com.pelisat.cesp.ceemsp.database.repository.ClienteDomicilioRepository;
import com.pelisat.cesp.ceemsp.database.repository.ClienteRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteAsignacionPersonalServiceImpl implements ClienteAsignacionPersonalService {
    private final ClienteAsignacionPersonalRepository clienteAsignacionPersonalRepository;
    private final UsuarioService usuarioService;
    private final PersonaService personaService;
    private final ClienteRepository clienteRepository;
    private final ClienteDomicilioService clienteDomicilioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(ClienteAsignacionPersonalServiceImpl.class);

    @Autowired
    public ClienteAsignacionPersonalServiceImpl(ClienteAsignacionPersonalRepository clienteAsignacionPersonalRepository, UsuarioService usuarioService,
                                                PersonaService personaService, ClienteRepository clienteRepository, ClienteDomicilioService clienteDomicilioService,
                                                DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper) {
        this.clienteAsignacionPersonalRepository = clienteAsignacionPersonalRepository;
        this.usuarioService = usuarioService;
        this.personaService = personaService;
        this.clienteRepository = clienteRepository;
        this.clienteDomicilioService = clienteDomicilioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<ClienteAsignacionPersonalDto> obtenerAsignacionesCliente(String empresaUuid, String clienteUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las asignaciones de personal con el cliente [{}]", clienteUuid);
        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<ClienteAsignacionPersonal> asignaciones = clienteAsignacionPersonalRepository.getAllByClienteAndEliminadoFalse(cliente.getId());

        return asignaciones.stream().map(a -> {
            ClienteAsignacionPersonalDto capd = new ClienteAsignacionPersonalDto();
            capd.setId(a.getId());
            capd.setUuid(a.getUuid());
            capd.setPersonal(personaService.obtenerPorId(a.getPersonal()));
            capd.setDomicilio(clienteDomicilioService.obtenerPorId(a.getClienteDomicilio()));
            return capd;
        }).collect(Collectors.toList());
    }

    @Override
    public ClienteAsignacionPersonalDto obtenerAsignacionPorUuid(String empresaUuid, String clienteUuid, String asignacionUuid) {
        return null;
    }

    @Override
    public ClienteAsignacionPersonalDto crearAsignacion(String empresaUUid, String clienteUuid, String username, ClienteAsignacionPersonalDto clienteAsignacionPersonalDto) {
        if(StringUtils.isBlank(empresaUUid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(username) || clienteAsignacionPersonalDto == null) {
            logger.warn("Alguno de los parametros estan invalidos");
            throw new InvalidDataException();
        }

        logger.info("Creando una nueva asigancion al cliente [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        ClienteAsignacionPersonal clienteAsignacionPersonal = new ClienteAsignacionPersonal();
        clienteAsignacionPersonal.setUuid(RandomStringUtils.randomAlphanumeric(12));
        clienteAsignacionPersonal.setCliente(cliente.getId());
        clienteAsignacionPersonal.setPersonal(clienteAsignacionPersonalDto.getPersonal().getId());
        clienteAsignacionPersonal.setClienteDomicilio(clienteAsignacionPersonalDto.getDomicilio().getId());
        daoHelper.fulfillAuditorFields(true, clienteAsignacionPersonal, usuarioDto.getId());

        ClienteAsignacionPersonal asignacionCreada = clienteAsignacionPersonalRepository.save(clienteAsignacionPersonal);

        clienteAsignacionPersonalDto.setUuid(asignacionCreada.getUuid());
        clienteAsignacionPersonalDto.setId(asignacionCreada.getId());
        return clienteAsignacionPersonalDto;
    }

    @Override
    public ClienteAsignacionPersonalDto modificarAsignacion(String empresaUuid, String clienteUuid, String asignacionUuid, String username, ClienteAsignacionPersonalDto clienteAsignacionPersonalDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(asignacionUuid) || StringUtils.isBlank(username) || clienteAsignacionPersonalDto == null) {
            logger.warn("Alguno de los parametros estan nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Modificando la asignacion con el uuid [{}]", asignacionUuid);

        ClienteAsignacionPersonal clienteAsignacionPersonal = clienteAsignacionPersonalRepository.getByUuidAndEliminadoFalse(asignacionUuid);

        if(clienteAsignacionPersonal == null) {
            logger.warn("La asignacion no existe o se encuentra como nula");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        clienteAsignacionPersonal.setPersonal(clienteAsignacionPersonalDto.getPersonal().getId());
        clienteAsignacionPersonal.setClienteDomicilio(clienteAsignacionPersonalDto.getDomicilio().getId());
        daoHelper.fulfillAuditorFields(false, clienteAsignacionPersonal, usuarioDto.getId());
        clienteAsignacionPersonalRepository.save(clienteAsignacionPersonal);

        return clienteAsignacionPersonalDto;
    }

    @Override
    public ClienteAsignacionPersonalDto eliminarAsignacion(String empresaUuid, String clienteUuid, String asignacionUUid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(asignacionUUid) || StringUtils.isBlank(username)) {
            logger.warn("Hay un problema con alguno de los parametros enviados");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la asignacion con el uuid [{}]", asignacionUUid);

        ClienteAsignacionPersonal asignacion = clienteAsignacionPersonalRepository.getByUuidAndEliminadoFalse(asignacionUUid);

        if(asignacion == null) {
            logger.warn("La asignacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        asignacion.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, asignacion, usuarioDto.getId());
        clienteAsignacionPersonalRepository.save(asignacion);

        return null;
    }
}
