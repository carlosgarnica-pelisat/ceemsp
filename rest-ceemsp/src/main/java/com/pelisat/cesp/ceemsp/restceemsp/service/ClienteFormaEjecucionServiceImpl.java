package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.ClienteFormaEjecucion;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.ClienteFormaEjecucionRepository;
import com.pelisat.cesp.ceemsp.database.repository.ClienteRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteFormaEjecucionServiceImpl implements ClienteFormaEjecucionService {

    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final DaoHelper<CommonModel> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(ClienteFormaEjecucionServiceImpl.class);
    private final ClienteFormaEjecucionRepository clienteFormaEjecucionRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;

    @Autowired
    public ClienteFormaEjecucionServiceImpl(
            UsuarioService usuarioService, ClienteRepository clienteRepository, DaoHelper<CommonModel> daoHelper,
            ClienteFormaEjecucionRepository clienteFormaEjecucionRepository, DaoToDtoConverter daoToDtoConverter,
            DtoToDaoConverter dtoToDaoConverter
    ) {
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
        this.daoHelper = daoHelper;
        this.clienteFormaEjecucionRepository = clienteFormaEjecucionRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<ClienteFormaEjecucionDto> obtenerFormasEjecucionPorClienteUuid(String uuid, String clienteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las formas de ejecucion para el cliente [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuid(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<ClienteFormaEjecucion> formasEjecucionCliente = clienteFormaEjecucionRepository.getAllByClienteAndEliminadoFalse(cliente.getId());

        return formasEjecucionCliente.stream().map(daoToDtoConverter::convertDaoToDtoClienteFormaEjecucion).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteFormaEjecucionDto crearFormaEjecucion(String uuid, String clienteUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(username) || clienteFormaEjecucionDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Creando forma de ejecucion en el cliente [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuid(clienteUuid);
        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        ClienteFormaEjecucion clienteFormaEjecucion = dtoToDaoConverter.convertDtoToDaoClienteFormaEjecucion(clienteFormaEjecucionDto);
        clienteFormaEjecucion.setCliente(cliente.getId());
        daoHelper.fulfillAuditorFields(true, clienteFormaEjecucion, usuarioDto.getId());

        ClienteFormaEjecucion formaEjecucionCreada = clienteFormaEjecucionRepository.save(clienteFormaEjecucion);

        if(!cliente.isFormaEjecucionCapturada()) {
            cliente.setFormaEjecucionCapturada(true);
            daoHelper.fulfillAuditorFields(false, cliente, usuarioDto.getId());
            clienteRepository.save(cliente);
        }

        return daoToDtoConverter.convertDaoToDtoClienteFormaEjecucion(formaEjecucionCreada);
    }

    @Override
    @Transactional
    public ClienteFormaEjecucionDto modificarFormaEjecucion(String uuid, String clienteUuid, String formaEjecucionUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(formaEjecucionUuid) || StringUtils.isBlank(username) || clienteFormaEjecucionDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Modificando la forma de ejecucion con el uuid [{}]", formaEjecucionUuid);

        ClienteFormaEjecucion clienteFormaEjecucion = clienteFormaEjecucionRepository.getByUuidAndEliminadoFalse(formaEjecucionUuid);

        if(clienteFormaEjecucion == null) {
            logger.warn("La forma de ejecucion del cliente viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        clienteFormaEjecucion.setFormaEjecucion(clienteFormaEjecucionDto.getFormaEjecucion());
        daoHelper.fulfillAuditorFields(false, clienteFormaEjecucion, usuarioDto.getId());
        clienteFormaEjecucionRepository.save(clienteFormaEjecucion);

        return clienteFormaEjecucionDto;
    }

    @Override
    @Transactional
    public ClienteFormaEjecucionDto eliminarFormaEjecucion(String uuid, String clienteUuid, String formaEjecucionUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(formaEjecucionUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la forma de ejecucion con el uuid [{}]", formaEjecucionUuid);

        ClienteFormaEjecucion clienteFormaEjecucion = clienteFormaEjecucionRepository.getByUuidAndEliminadoFalse(formaEjecucionUuid);

        if(clienteFormaEjecucion == null) {
            logger.warn("La forma de ejecucion del cliente viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        clienteFormaEjecucion.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, clienteFormaEjecucion, usuarioDto.getId());
        clienteFormaEjecucionRepository.save(clienteFormaEjecucion);

        return daoToDtoConverter.convertDaoToDtoClienteFormaEjecucion(clienteFormaEjecucion);
    }
}
