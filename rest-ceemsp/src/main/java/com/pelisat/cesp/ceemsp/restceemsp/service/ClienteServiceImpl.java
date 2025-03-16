package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.ClienteRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final ClienteAsignacionPersonalService clienteAsignacionPersonalService;
    private final ArchivosService archivosService;
    private final ClienteModalidadService clienteModalidadService;
    private final ClienteFormaEjecucionService clienteFormaEjecucionService;

    private final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    public ClienteServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                              ClienteRepository clienteRepository, UsuarioService usuarioService,
                              EmpresaService empresaService, DaoHelper<CommonModel> daoHelper,
                              ClienteDomicilioService clienteDomicilioService, ArchivosService archivosService,
                              ClienteAsignacionPersonalService clienteAsignacionPersonalService, ClienteModalidadService clienteModalidadService,
                              ClienteFormaEjecucionService clienteFormaEjecucionService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.daoHelper = daoHelper;
        this.clienteDomicilioService = clienteDomicilioService;
        this.archivosService = archivosService;
        this.clienteAsignacionPersonalService = clienteAsignacionPersonalService;
        this.clienteModalidadService = clienteModalidadService;
        this.clienteFormaEjecucionService = clienteFormaEjecucionService;
    }


    @Override
    public List<ClienteDto> obtenerClientesPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Cliente> clientes = clienteRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());
        return clientes.stream().map(c -> {
                ClienteDto clienteDto = daoToDtoConverter.convertDaoToDtoCliente(c);
                List<ClienteDomicilioDto> domicilios = clienteDomicilioService.obtenerDomiciliosPorCliente(c.getId());
                List<ClienteAsignacionPersonalDto> asignacionPersonalDtos = clienteAsignacionPersonalService.obtenerAsignacionesCliente(empresaUuid, c.getUuid());
                clienteDto.setNumeroSucursales(domicilios.size());
                clienteDto.setNumeroElementosAsignados(asignacionPersonalDtos.size());
                return clienteDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClienteDto> obtenerClientesEliminadosPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Cliente> clientes = clienteRepository.findAllByEmpresaAndEliminadoTrue(empresaDto.getId());
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
        Cliente cliente = clienteRepository.findByUuid(clienteUuid);

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
            response.setAsignaciones(clienteAsignacionPersonalService.obtenerAsignacionesCliente(empresaUuid, clienteUuid));
            response.setModalidades(clienteModalidadService.obtenerModalidadesPorCliente(empresaUuid, clienteUuid));
            response.setFormasEjecucion(clienteFormaEjecucionService.obtenerFormasEjecucionPorClienteUuid(empresaUuid, clienteUuid));
        }

        return response;
    }

    @Override
    public File obtenerContrato(String empresaUuid, String clienteUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando el contrato en PDF para la escritura [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        if(cliente == null) {
            logger.warn("La escritura no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(cliente.getRutaArchivoContrato());
    }

    @Override
    public File descargarDocumentoFundatorio(String empresaUuid, String clienteUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid de la empresa o del vehiculo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando el documento fundatorio para el cliente [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuid(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        if(!cliente.getEliminado()) {
            logger.warn("El cliente no esta eliminado. Esta funcion no es compatible");
            throw new NotFoundResourceException();
        }

        return new File(cliente.getDocumentoFundatorioBaja());
    }

    @Transactional
    @Override
    public ClienteDto crearCliente(String empresaUuid, String username, ClienteDto clienteDto, MultipartFile archivo) {
        if(StringUtils.isBlank(empresaUuid) || clienteDto == null || StringUtils.isBlank(username)) {
            logger.warn("El uuid o el cliente a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Cliente cliente = dtoToDaoConverter.convertDtoToDaoCliente(clienteDto);
        cliente.setEmpresa(empresaDto.getId());
        cliente.setFechaInicio(LocalDate.parse(clienteDto.getFechaInicio()));
        if(StringUtils.isNotBlank(clienteDto.getFechaFin())) {
            cliente.setFechaFin(LocalDate.parse(clienteDto.getFechaFin()));
        }
        daoHelper.fulfillAuditorFields(true, cliente, usuarioDto.getId());

        if(archivo != null) {
            String ruta = "";

            try {
                ruta = archivosService.guardarArchivoMultipart(archivo, TipoArchivoEnum.CLIENTE_CONTRATO_SERVICIOS, empresaUuid);
                cliente.setRutaArchivoContrato(ruta);
            } catch(Exception ex) {
                logger.warn(ex.getMessage());
                archivosService.eliminarArchivo(ruta);
                throw new InvalidDataException();
            }
        }
        Cliente clienteCreado = clienteRepository.save(cliente);
        return daoToDtoConverter.convertDaoToDtoCliente(clienteCreado);
    }

    @Override
    @Transactional
    public ClienteDto modificarCliente(String empresaUuid, String clienteUuid, String username, ClienteDto clienteDto) {
        if(StringUtils.isBlank(empresaUuid) || clienteDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid o el cliente a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el cliente con el uuid [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);
        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        cliente.setFechaInicio(LocalDate.parse(clienteDto.getFechaInicio()));
        cliente.setRazonSocial(clienteDto.getRazonSocial());
        cliente.setNombreComercial(clienteDto.getNombreComercial());
        cliente.setRfc(clienteDto.getRfc());
        cliente.setTipoPersona(clienteDto.getTipoPersona());
        cliente.setArmas(clienteDto.isArmas());
        cliente.setCanes(clienteDto.isCanes());

        if(clienteDto.getFechaFin() != null) {
            cliente.setFechaFin(LocalDate.parse(clienteDto.getFechaFin()));
        }

        daoHelper.fulfillAuditorFields(false, cliente, usuarioDto.getId());

        clienteRepository.save(cliente);
        return daoToDtoConverter.convertDaoToDtoCliente(cliente);
    }

    @Override
    @Transactional
    public ClienteDto eliminarCliente(String empresaUuid, String clienteUuid, String username, ClienteDto clienteDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid o el cliente a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el cliente con el uuid [{}]", clienteUuid);

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);
        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        logger.info("Verificando si hay asignaciones");
        List<ClienteAsignacionPersonalDto> asignacionPersonal = clienteAsignacionPersonalService.obtenerAsignacionesCliente(empresaUuid, clienteUuid);
        if(!asignacionPersonal.isEmpty()) {
            asignacionPersonal.forEach(ap -> {
                clienteAsignacionPersonalService.eliminarAsignacion(empresaUuid, clienteUuid, ap.getUuid(), username);
            });
        }

        cliente.setMotivoBaja(clienteDto.getMotivoBaja());
        cliente.setObservacionesBaja(clienteDto.getObservacionesBaja());
        cliente.setFechaBaja(LocalDate.now());
        cliente.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, cliente, usuarioDto.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_CLIENTE, empresaUuid);
                cliente.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        clienteRepository.save(cliente);
        return daoToDtoConverter.convertDaoToDtoCliente(cliente);
    }
}
