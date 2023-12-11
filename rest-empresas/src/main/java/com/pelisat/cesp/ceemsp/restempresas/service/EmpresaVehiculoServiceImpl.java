package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.model.VehiculoDomicilio;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoDomicilioRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.database.type.VehiculoStatusEnum;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final EmpresaDomicilioService empresaDomicilioService;
    private final CatalogoService catalogoService;
    private final EmpresaVehiculoFotografiaService empresaVehiculoFotografiaService;
    private final EmpresaVehiculoColorService empresaVehiculoColorService;
    private final ArchivosService archivosService;
    private final VehiculoDomicilioRepository vehiculoDomicilioRepository;

    @Autowired
    public EmpresaVehiculoServiceImpl(VehiculoRepository vehiculoRepository, UsuarioService usuarioService,
                                      DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                      DaoHelper<CommonModel> daoHelper, EmpresaDomicilioService empresaDomicilioService,
                                      EmpresaVehiculoFotografiaService empresaVehiculoFotografiaService,
                                      EmpresaVehiculoColorService empresaVehiculoColorService, CatalogoService catalogoService,
                                      ArchivosService archivosService, VehiculoDomicilioRepository vehiculoDomicilioRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaDomicilioService = empresaDomicilioService;
        this.empresaVehiculoColorService = empresaVehiculoColorService;
        this.empresaVehiculoFotografiaService = empresaVehiculoFotografiaService;
        this.catalogoService = catalogoService;
        this.archivosService = archivosService;
        this.vehiculoDomicilioRepository = vehiculoDomicilioRepository;
    }

    @Override
    public List<VehiculoDto> obtenerVehiculos(String empresaUsername) {
        if(StringUtils.isBlank(empresaUsername)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando todos los vehiculos");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(empresaUsername);
        List<Vehiculo> vehiculos = vehiculoRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        List<VehiculoDto> response = vehiculos.stream().map(vehiculo -> {
            VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
            vehiculoDto.setMarca(catalogoService.obtenerMarcaPorId(vehiculo.getMarca()));
            if(vehiculo.getSubmarca() > 0) {
                vehiculoDto.setSubmarca(catalogoService.obtenerSubmarcaPorId(vehiculo.getSubmarca()));
            }
            vehiculoDto.setTipo(catalogoService.obtenerTipoVehiculoPorId(vehiculo.getTipo()));
            vehiculoDto.setFotografias(empresaVehiculoFotografiaService.mostrarVehiculoFotografias(vehiculo.getUuid()));
            return vehiculoDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public List<VehiculoDto> obtenerVehiculosEnInstalacionesPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando todos los vehiculos");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Vehiculo> vehiculos = vehiculoRepository.getAllByEmpresaAndStatusAndEliminadoFalse(usuarioDto.getEmpresa().getId(), VehiculoStatusEnum.INSTALACIONES);
        return vehiculos.stream().map(vehiculo -> {
            VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
            vehiculoDto.setMarca(catalogoService.obtenerMarcaPorId(vehiculo.getMarca()));
            if(vehiculo.getSubmarca() > 0) {
                vehiculoDto.setSubmarca(catalogoService.obtenerSubmarcaPorId(vehiculo.getSubmarca()));
            }
            vehiculoDto.setTipo(catalogoService.obtenerTipoVehiculoPorId(vehiculo.getTipo()));
            return vehiculoDto;
        }).collect(Collectors.toList());
    }

    @Override
    public VehiculoDto obtenerVehiculoPorUuid(String vehiculoUuid) {
        if(StringUtils.isBlank(vehiculoUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el vehiculo con el uuid [{}]", vehiculoUuid);

        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);

        VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
        vehiculoDto.setUso(catalogoService.obtenerUsoVehiculoPorId(vehiculo.getUso()));
        vehiculoDto.setMarca(catalogoService.obtenerMarcaPorId(vehiculo.getMarca()));
        if(vehiculo.getSubmarca() > 0) {
            vehiculoDto.setSubmarca(catalogoService.obtenerSubmarcaPorId(vehiculo.getSubmarca()));
        }

        vehiculoDto.setTipo(catalogoService.obtenerTipoVehiculoPorId(vehiculo.getTipo()));
        vehiculoDto.setDomicilio(empresaDomicilioService.obtenerPorId(vehiculo.getDomicilio()));
        vehiculoDto.setColores(empresaVehiculoColorService.obtenerTodosPorVehiculoUuid(vehiculoUuid));
        vehiculoDto.setFotografias(empresaVehiculoFotografiaService.mostrarVehiculoFotografias(vehiculo.getUuid()));

        return vehiculoDto;
    }

    @Override
    public VehiculoDto obtenerVehiculoPorId(Integer vehiculoId) {
        if(vehiculoId < 1) {
            logger.warn("El uuid de la empresa o el id del vehiculo a consultar vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el vehiculo con el id [{}]", vehiculoId);

        Vehiculo vehiculo = vehiculoRepository.getOne(vehiculoId);

        VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
        vehiculoDto.setUso(catalogoService.obtenerUsoVehiculoPorId(vehiculo.getUso()));
        vehiculoDto.setMarca(catalogoService.obtenerMarcaPorId(vehiculo.getMarca()));
        if(vehiculo.getSubmarca() > 0) {
            vehiculoDto.setSubmarca(catalogoService.obtenerSubmarcaPorId(vehiculo.getSubmarca()));
        }
        vehiculoDto.setTipo(catalogoService.obtenerTipoVehiculoPorId(vehiculo.getTipo()));
        vehiculoDto.setDomicilio(empresaDomicilioService.obtenerPorId(vehiculo.getDomicilio()));

        return vehiculoDto;
    }

    @Override
    @Transactional
    public VehiculoDto guardarVehiculo(String empresaUsername, VehiculoDto vehiculoDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUsername) || vehiculoDto == null) {
            logger.warn("El vehiculo, el uuid de la empresa o el nombre del usuario vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(empresaUsername);

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
        vehiculo.setEmpresa(usuarioDto.getEmpresa().getId());
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
        daoHelper.fulfillAuditorFields(true, vehiculo, usuarioDto.getId());

        if(multipartFile != null) {
            logger.info("Hay archivo");

            try {
                String ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.CONSTANCIA_BLINDAJE_VEHICULO, usuarioDto.getEmpresa().getUuid());
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
    public VehiculoDto modificarVehiculo(String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile constanciaBlindaje) {
        if(StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username) || vehiculoDto == null) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

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

        if(vehiculo.getDomicilio() != vehiculoDto.getDomicilio().getId()) {
            VehiculoDomicilio vehiculoDomicilio = new VehiculoDomicilio();
            vehiculoDomicilio.setVehiculo(vehiculo.getId());
            vehiculoDomicilio.setDomicilioActual(vehiculoDto.getDomicilio().getId());
            vehiculoDomicilio.setDomicilioAnterior(vehiculo.getDomicilio());
            daoHelper.fulfillAuditorFields(true, vehiculoDomicilio, usuarioDto.getId());
            vehiculoDomicilioRepository.save(vehiculoDomicilio);
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
                String ruta = archivosService.guardarArchivoMultipart(constanciaBlindaje, TipoArchivoEnum.CONSTANCIA_BLINDAJE_VEHICULO, usuarioDto.getEmpresa().getUuid());
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
    @Transactional
    public VehiculoDto eliminarVehiculo(String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        vehiculo.setMotivoBaja(vehiculoDto.getMotivoBaja());
        vehiculo.setObservacionesBaja(vehiculoDto.getObservacionesBaja());
        vehiculo.setFechaBaja(LocalDate.parse(vehiculoDto.getFechaBaja()));
        vehiculo.setEliminado(true);

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_VEHICULO, usuarioDto.getEmpresa().getUuid());
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

    @Override
    public File obtenerConstanciaBlindaje(String vehiculoUuid) {
        if(StringUtils.isBlank(vehiculoUuid)) {
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
}
