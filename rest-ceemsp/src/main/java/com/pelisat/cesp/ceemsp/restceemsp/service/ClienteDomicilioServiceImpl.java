package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.ClienteDomicilio;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.ClienteDomicilioRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteDomicilioServiceImpl implements ClienteDomicilioService {

    private final Logger logger = LoggerFactory.getLogger(ClienteDomicilioService.class);
    private final ClienteDomicilioRepository clienteDomicilioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteDomicilioServiceImpl(ClienteDomicilioRepository clienteDomicilioRepository, DaoToDtoConverter daoToDtoConverter,
                                   DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                   ClienteRepository clienteRepository) {
        this.clienteDomicilioRepository = clienteDomicilioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<ClienteDomicilioDto> obtenerDomiciliosPorCliente(int clienteId) {
        if(clienteId < 1) {
            logger.warn("El id del cliente viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los domicilios del cliente con id [{}]", clienteId);

        List<ClienteDomicilio> clienteDomicilios = clienteDomicilioRepository.getAllByClienteAndEliminadoFalse(clienteId);

        if(clienteDomicilios == null || clienteDomicilios.size() < 1) {
            logger.warn("No hay domicilios guardados para este cliente");
            throw new NotFoundResourceException();
        }

        return clienteDomicilios.stream().map(daoToDtoConverter::convertDaoToDtoClienteDomicilio).collect(Collectors.toList());
    }

    @Override
    public List<ClienteDomicilioDto> obtenerDomiciliosPorClienteUuid(String clienteUuid) {
        if(StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid del cliente viene como nula o vacia");
            throw new InvalidDataException();
        }

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        return obtenerDomiciliosPorCliente(cliente.getId());
    }

    @Override
    public List<ClienteDomicilioDto> crearDomicilio(String username, String clienteUuid, List<ClienteDomicilioDto> clienteDomicilioDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(clienteUuid) || clienteDomicilioDto == null) {
            logger.warn("Hay alguno de los parametros que no es valido");
            throw new InvalidDataException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);
        if(cliente == null) {
            logger.warn("El cliente viene como nulo en la base de datos");
            throw new NotFoundResourceException();
        }

        List<ClienteDomicilio> clienteDomicilios = clienteDomicilioDto.stream().map(m -> {
            ClienteDomicilio c = dtoToDaoConverter.convertDtoToDaoClienteDomicilio(m);
            daoHelper.fulfillAuditorFields(true, c, usuario.getId());
            c.setCliente(cliente.getId());
            return c;
        }).collect(Collectors.toList());
        List<ClienteDomicilio> clienteDomicilioCreado = clienteDomicilioRepository.saveAll(clienteDomicilios);

        return clienteDomicilios.stream().map(daoToDtoConverter::convertDaoToDtoClienteDomicilio).collect(Collectors.toList());
    }
}
