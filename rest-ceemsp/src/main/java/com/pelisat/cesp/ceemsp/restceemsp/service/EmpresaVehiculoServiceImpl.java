package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.IncidenciaArchivo;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoSubmarcaRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.*;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmpresaVehiculoServiceImpl implements EmpresaVehiculoService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaVehiculoService.class);
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final VehiculoMarcaService vehiculoMarcaService;
    private final VehiculoSubmarcaService vehiculoSubmarcaService;
    private final VehiculoUsoService vehiculoUsoService;
    private final VehiculoTipoService vehiculoTipoService;
    private final VehiculoColorService vehiculoColorService;
    private final VehiculoFotografiaService vehiculoFotografiaService;
    private final ArchivosService archivosService;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final PersonaService personaService;

    @Autowired
    public EmpresaVehiculoServiceImpl(VehiculoRepository vehiculoRepository, UsuarioService usuarioService,
                                      DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                      DaoHelper<CommonModel> daoHelper, EmpresaService empresaService,
                                      VehiculoMarcaService vehiculoMarcaService, VehiculoSubmarcaService vehiculoSubmarcaService,
                                      VehiculoUsoService vehiculoUsoService, VehiculoTipoService vehiculoTipoService,
                                      VehiculoColorService vehiculoColorService, VehiculoFotografiaService vehiculoFotografiaService,
                                      ArchivosService archivosService, EmpresaDomicilioService empresaDomicilioService,
                                      PersonaService personaService) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.vehiculoMarcaService = vehiculoMarcaService;
        this.vehiculoSubmarcaService = vehiculoSubmarcaService;
        this.vehiculoUsoService = vehiculoUsoService;
        this.vehiculoTipoService = vehiculoTipoService;
        this.vehiculoColorService = vehiculoColorService;
        this.vehiculoFotografiaService = vehiculoFotografiaService;
        this.archivosService = archivosService;
        this.empresaDomicilioService = empresaDomicilioService;
        this.personaService = personaService;
    }

    @Override
    public List<VehiculoDto> obtenerVehiculosPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando todos los vehiculos");

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Vehiculo> vehiculos = vehiculoRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        List<VehiculoDto> response = vehiculos.stream().map(vehiculo -> {
            VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
            vehiculoDto.setMarca(vehiculoMarcaService.obtenerPorId(vehiculo.getMarca()));
            if(vehiculo.getSubmarca() > 0) {
                vehiculoDto.setSubmarca(vehiculoSubmarcaService.obtenerPorId(vehiculo.getSubmarca()));
            }
            vehiculoDto.setTipo(vehiculoTipoService.obtenerPorId(vehiculo.getTipo()));
            vehiculoDto.setFotografias(vehiculoFotografiaService.mostrarVehiculoFotografias(empresaUuid, vehiculo.getUuid()));
            return vehiculoDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public List<VehiculoDto> obtenerVehiculosEliminadosPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando todos los vehiculos");

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Vehiculo> vehiculos = vehiculoRepository.getAllByEmpresaAndEliminadoTrue(empresaDto.getId());

        List<VehiculoDto> response = vehiculos.stream().map(vehiculo -> {
            VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
            vehiculoDto.setMarca(vehiculoMarcaService.obtenerPorId(vehiculo.getMarca()));
            if(vehiculo.getSubmarca() > 0) {
                vehiculoDto.setSubmarca(vehiculoSubmarcaService.obtenerPorId(vehiculo.getSubmarca()));
            }
            vehiculoDto.setTipo(vehiculoTipoService.obtenerPorId(vehiculo.getTipo()));
            vehiculoDto.setFotografias(vehiculoFotografiaService.mostrarVehiculoFotografias(empresaUuid, vehiculo.getUuid()));
            return vehiculoDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public VehiculoDto obtenerVehiculoPorUuid(String empresaUuid, String vehiculoUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(vehiculoUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el vehiculo con el uuid [{}]", vehiculoUuid);

        EmpresaDto empresa = empresaService.obtenerPorUuid(empresaUuid);
        Vehiculo vehiculo = vehiculoRepository.getByUuid(vehiculoUuid);

        if(empresa.getId() != vehiculo.getEmpresa()) {
            logger.warn("El vehiculo no pertenece a esa empresa");
            throw new MissingRelationshipException();
        }

        VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
        if(!soloEntidad) {
            vehiculoDto.setUso(vehiculoUsoService.obtenerPorId(vehiculo.getUso()));
            vehiculoDto.setMarca(vehiculoMarcaService.obtenerPorId(vehiculo.getMarca()));
            if(vehiculo.getSubmarca() > 0) {
                vehiculoDto.setSubmarca(vehiculoSubmarcaService.obtenerPorId(vehiculo.getSubmarca()));
            }
            vehiculoDto.setTipo(vehiculoTipoService.obtenerPorId(vehiculo.getTipo()));
            vehiculoDto.setDomicilio(empresaDomicilioService.obtenerPorId(vehiculo.getDomicilio()));
            vehiculoDto.setColores(vehiculoColorService.obtenerTodosPorVehiculoUuid(vehiculoUuid, empresaUuid));
            vehiculoDto.setFotografias(vehiculoFotografiaService.mostrarVehiculoFotografias(empresaUuid, vehiculo.getUuid()));
        }

        return vehiculoDto;
    }

    @Override
    public File obtenerConstanciaBlindaje(String empresaUuid, String vehiculoUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(vehiculoUuid)) {
            logger.warn("El uuid de la empresa o del vehiculo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando la constancia en blindaje en PDF para la escritura [{}]", vehiculoUuid);

        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);

        if(vehiculo == null) {
            logger.warn("La escritura no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(vehiculo.getConstanciaBlindaje());
    }

    @Override
    public VehiculoDto obtenerVehiculoPorId(String empresaUuid, Integer vehiculoId) {
        if(StringUtils.isBlank(empresaUuid) || vehiculoId == null || vehiculoId < 1) {
            logger.warn("El uuid de la empresa o el id del vehiculo a consultar vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el vehiculo con el id [{}]", vehiculoId);

        Vehiculo vehiculo = vehiculoRepository.getOne(vehiculoId);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
        vehiculoDto.setUso(vehiculoUsoService.obtenerPorId(vehiculo.getUso()));
        vehiculoDto.setMarca(vehiculoMarcaService.obtenerPorId(vehiculo.getMarca()));
        if(vehiculo.getSubmarca() > 0) {
            vehiculoDto.setSubmarca(vehiculoSubmarcaService.obtenerPorId(vehiculo.getSubmarca()));
        }
        vehiculoDto.setTipo(vehiculoTipoService.obtenerPorId(vehiculo.getTipo()));
        vehiculoDto.setDomicilio(empresaDomicilioService.obtenerPorId(vehiculo.getDomicilio()));

        return vehiculoDto;
    }

    @Transactional
    @Override
    public VehiculoDto guardarVehiculo(String empresaUuid, String username, VehiculoDto vehiculoDto, MultipartFile constanciaBlindaje) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(username) || vehiculoDto == null) {
            logger.warn("El vehiculo, el uuid de la empresa o el nombre del usuario vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        logger.info("Verificando si el vehiculo ha sido verificado previamente");
        Vehiculo vehiculoPorPlacas = vehiculoRepository.getByPlacasAndEliminadoFalse(vehiculoDto.getPlacas());
        if(vehiculoPorPlacas != null) {
            logger.warn("Este vehiculo ya se encuentra registrado con este numero de placas");
            throw new VehicleAlreadyRegisteredByPlatesException();
        }

        Vehiculo vehiculoPorSerie = vehiculoRepository.getBySerieAndEliminadoFalse(vehiculoDto.getSerie());
        if(vehiculoPorSerie != null) {
            logger.warn("Este vehiculo ya se encuentra registrado con este numero de serie");
            throw new VehicleAlreadyRegisteredBySerialException();
        }

        Vehiculo vehiculo = dtoToDaoConverter.convertDtoToDaoVehiculo(vehiculoDto);
        vehiculo.setEmpresa(empresaDto.getId());
        vehiculo.setMarca(vehiculoDto.getMarca().getId());
        if(vehiculoDto.getSubmarca() != null) {
            vehiculo.setSubmarca(vehiculoDto.getSubmarca().getId());
        }

        vehiculo.setUso(vehiculoDto.getUso().getId());
        vehiculo.setTipo(vehiculoDto.getTipo().getId());
        vehiculo.setDomicilio(vehiculoDto.getDomicilio().getId());
        if(StringUtils.isNotBlank(vehiculoDto.getFechaInicio())) {
            vehiculo.setFechaInicio(LocalDate.parse(vehiculoDto.getFechaInicio()));
        }
        if(StringUtils.isNotBlank(vehiculoDto.getFechaFin())) {
            vehiculo.setFechaFin(LocalDate.parse(vehiculoDto.getFechaFin()));
        }
        if(StringUtils.isNotBlank(vehiculoDto.getFechaBlindaje())) {
            vehiculo.setFechaBlindaje(LocalDate.parse(vehiculoDto.getFechaBlindaje()));
        }

        if(constanciaBlindaje != null) {
            logger.info("Hay archivo");
            daoHelper.fulfillAuditorFields(true, vehiculo, usuarioDto.getId());
            try {
                String ruta = archivosService.guardarArchivoMultipart(constanciaBlindaje, TipoArchivoEnum.CONSTANCIA_BLINDAJE_VEHICULO, empresaDto.getUuid());
                vehiculo.setConstanciaBlindaje(ruta);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo.", ex);
                throw new InvalidDataException();
            }
        }
        Vehiculo vehiculoCreado = vehiculoRepository.save(vehiculo);
        VehiculoDto response = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoCreado);
        response.setTipo(vehiculoDto.getTipo());
        response.setMarca(vehiculoDto.getMarca());
        response.setSubmarca(vehiculoDto.getSubmarca());
        return response;
    }

    @Transactional
    @Override
    public VehiculoDto modificarVehiculo(String empresaUuid, String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile constanciaBlindaje) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username) || vehiculoDto == null) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(!Objects.equals(vehiculo.getPlacas(), vehiculoDto.getPlacas())) {
            logger.info("Verificando si el vehiculo ha sido verificado previamente");
            Vehiculo vehiculoPorPlacas = vehiculoRepository.getByPlacasAndEliminadoFalse(vehiculoDto.getPlacas());
            if(vehiculoPorPlacas != null) {
                logger.warn("Este vehiculo ya se encuentra registrado con este numero de placas");
                throw new VehicleAlreadyRegisteredByPlatesException();
            }
        }

        if(!Objects.equals(vehiculo.getSerie(), vehiculoDto.getSerie())) {
            Vehiculo vehiculoPorSerie = vehiculoRepository.getBySerieAndEliminadoFalse(vehiculoDto.getSerie());
            if(vehiculoPorSerie != null) {
                logger.warn("Este vehiculo ya se encuentra registrado con este numero de serie");
                throw new VehicleAlreadyRegisteredBySerialException();
            }
        }

        if(empresaDto.getId() != vehiculo.getEmpresa()) {
            logger.warn("El vehiculo no pertenece a esa empresa");
            throw new MissingRelationshipException();
        }

        vehiculo.setPlacas(vehiculoDto.getPlacas());
        vehiculo.setSerie(vehiculoDto.getSerie());
        vehiculo.setRotulado(vehiculoDto.isRotulado());
        vehiculo.setAnio(vehiculoDto.getAnio());
        vehiculo.setOrigen(vehiculoDto.getOrigen());
        vehiculo.setRazonSocial(vehiculoDto.getRazonSocial());

        if(StringUtils.isNotBlank(vehiculoDto.getFechaInicio())) {
            vehiculo.setFechaInicio(LocalDate.parse(vehiculoDto.getFechaInicio()));
        }
        if(StringUtils.isNotBlank(vehiculoDto.getFechaFin())) {
            vehiculo.setFechaFin(LocalDate.parse(vehiculoDto.getFechaFin()));
        }

        vehiculo.setBlindado(vehiculoDto.isBlindado());
        vehiculo.setSerieBlindaje(vehiculoDto.getSerieBlindaje());
        vehiculo.setNumeroHolograma(vehiculoDto.getNumeroHolograma());
        vehiculo.setPlacaMetalica(vehiculoDto.getPlacaMetalica());
        vehiculo.setEmpresaBlindaje(vehiculoDto.getEmpresaBlindaje());
        vehiculo.setNivelBlindaje(vehiculoDto.getNivelBlindaje());

        if(StringUtils.isNotBlank(vehiculoDto.getFechaBlindaje())) {
            vehiculo.setFechaBlindaje(LocalDate.parse(vehiculoDto.getFechaBlindaje()));
        }

        vehiculo.setMarca(vehiculoDto.getMarca().getId());
        if(vehiculoDto.getSubmarca() != null) {
            vehiculo.setSubmarca(vehiculoDto.getSubmarca().getId());
        }

        vehiculo.setUso(vehiculoDto.getUso().getId());
        vehiculo.setTipo(vehiculoDto.getTipo().getId());
        vehiculo.setDomicilio(vehiculoDto.getDomicilio().getId());

        if(constanciaBlindaje != null) {
            logger.info("Hay archivo");
            daoHelper.fulfillAuditorFields(true, vehiculo, usuarioDto.getId());
            try {
                String ruta = archivosService.guardarArchivoMultipart(constanciaBlindaje, TipoArchivoEnum.CONSTANCIA_BLINDAJE_VEHICULO, empresaDto.getUuid());
                vehiculo.setConstanciaBlindaje(ruta);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo.", ex);
                throw new InvalidDataException();
            }
        }

        daoHelper.fulfillAuditorFields(false, vehiculo, usuarioDto.getId());

        Vehiculo vehiculoCreado = vehiculoRepository.save(vehiculo);
        return daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoCreado);
    }

    @Override
    public VehiculoDto eliminarVehiculo(String empresaUuid, String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(empresaDto.getId() != vehiculo.getEmpresa()) {
            logger.warn("El vehiculo no pertenece a esa empresa");
            throw new MissingRelationshipException();
        }

        vehiculo.setMotivoBaja(vehiculoDto.getMotivoBaja());
        vehiculo.setObservacionesBaja(vehiculoDto.getObservacionesBaja());
        vehiculo.setFechaBaja(LocalDate.parse(vehiculoDto.getFechaBaja()));
        vehiculo.setEliminado(true);

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_VEHICULO, empresaUuid);
                vehiculo.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        daoHelper.fulfillAuditorFields(false, vehiculo, usuarioDto.getId());

        Vehiculo vehiculoCreado = vehiculoRepository.save(vehiculo);
        return daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoCreado);
    }
}
