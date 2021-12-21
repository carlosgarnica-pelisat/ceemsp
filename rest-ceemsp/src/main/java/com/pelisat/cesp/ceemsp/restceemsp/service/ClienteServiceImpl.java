package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.repository.ClienteRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
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
public class ClienteServiceImpl implements ClienteService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final DaoHelper<CommonModel> daoHelper;
    private final ClienteDomicilioService clienteDomicilioService;

    private final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    public ClienteServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                              ClienteRepository clienteRepository, UsuarioService usuarioService,
                              EmpresaService empresaService, DaoHelper<CommonModel> daoHelper,
                              ClienteDomicilioService clienteDomicilioService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.daoHelper = daoHelper;
        this.clienteDomicilioService = clienteDomicilioService;
    }


    @Override
    public List<ClienteDto> obtenerClientesPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Cliente> clientes = clienteRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());
        return clientes.stream().map(daoToDtoConverter::convertDaoToDtoCliente)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteDto obtenerClientePorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el cliente con el id [{}]", id);
        Cliente cliente = clienteRepository.getOne(id);

        if(cliente == null || cliente.getEliminado()) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCliente(cliente);
    }

    @Override
    public ClienteDto obtenerClientePorUuid(String empresaUuid, String clienteUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid de la empresa o del cliente vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        if(cliente.getEmpresa() != empresaDto.getId()) {
            logger.warn("El cliente no pertenece a la empresa");
            throw new MissingRelationshipException();
        }

        ClienteDto response = daoToDtoConverter.convertDaoToDtoCliente(cliente);

        if(!soloEntidad) {
            response.setDomicilios(clienteDomicilioService.obtenerDomiciliosPorCliente(response.getId()));
        }

        return response;
    }

    @Override
    public ClienteDto crearCliente(String empresaUuid, String username, ClienteDto clienteDto) {
        if(StringUtils.isBlank(empresaUuid) || clienteDto == null || StringUtils.isBlank(username)) {
            logger.warn("El uuid o el cliente a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Cliente cliente = dtoToDaoConverter.convertDtoToDaoCliente(clienteDto);
        cliente.setEmpresa(empresaDto.getId());
        daoHelper.fulfillAuditorFields(true, cliente, usuarioDto.getId());

        Cliente clienteCreado = clienteRepository.save(cliente);

        return daoToDtoConverter.convertDaoToDtoCliente(cliente);
    }
}
