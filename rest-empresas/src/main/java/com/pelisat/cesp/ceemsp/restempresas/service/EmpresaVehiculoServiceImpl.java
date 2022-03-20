package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
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

import java.time.LocalDate;
import java.util.List;
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
    private final CatalogoService catalogoService;
    private final EmpresaVehiculoFotografiaService empresaVehiculoFotografiaService;
    private final EmpresaVehiculoColorService empresaVehiculoColorService;

    @Autowired
    public EmpresaVehiculoServiceImpl(VehiculoRepository vehiculoRepository, UsuarioService usuarioService,
                                      DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                      DaoHelper<CommonModel> daoHelper, EmpresaService empresaService,
                                      EmpresaVehiculoFotografiaService empresaVehiculoFotografiaService,
                                      EmpresaVehiculoColorService empresaVehiculoColorService, CatalogoService catalogoService) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.empresaVehiculoColorService = empresaVehiculoColorService;
        this.empresaVehiculoFotografiaService = empresaVehiculoFotografiaService;
        this.catalogoService = catalogoService;
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
            vehiculoDto.setMarca(catalogoService.obtenerMarcaPorId(vehiculo.getId()));
            vehiculoDto.setSubmarca(catalogoService.obtenerSubmarcaPorId(vehiculo.getId()));
            vehiculoDto.setTipo(catalogoService.obtenerTipoVehiculoPorId(vehiculo.getTipo()));
            vehiculoDto.setFotografias(empresaVehiculoFotografiaService.mostrarVehiculoFotografias(vehiculo.getUuid()));
            return vehiculoDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public VehiculoDto obtenerVehiculoPorUuid(String empresaUsername, String vehiculoUuid) {
        if(StringUtils.isBlank(vehiculoUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el vehiculo con el uuid [{}]", vehiculoUuid);

        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);

        VehiculoDto vehiculoDto = daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo);
        vehiculoDto.setMarca(catalogoService.obtenerMarcaPorId(vehiculo.getMarca()));
        vehiculoDto.setSubmarca(catalogoService.obtenerSubmarcaPorId(vehiculo.getSubmarca()));
        vehiculoDto.setTipo(catalogoService.obtenerTipoVehiculoPorId(vehiculo.getTipo()));
        vehiculoDto.setColores(empresaVehiculoColorService.obtenerTodosPorVehiculoUuid(vehiculoUuid));
        vehiculoDto.setFotografias(empresaVehiculoFotografiaService.mostrarVehiculoFotografias(vehiculo.getUuid()));

        return vehiculoDto;
    }

    @Override
    public VehiculoDto guardarVehiculo(String empresaUsername, VehiculoDto vehiculoDto) {
        if(StringUtils.isBlank(empresaUsername) || vehiculoDto == null) {
            logger.warn("El vehiculo, el uuid de la empresa o el nombre del usuario vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(empresaUsername);

        Vehiculo vehiculo = dtoToDaoConverter.convertDtoToDaoVehiculo(vehiculoDto);
        vehiculo.setEmpresa(usuarioDto.getEmpresa().getId());
        vehiculo.setMarca(vehiculoDto.getMarca().getId());
        vehiculo.setSubmarca(vehiculoDto.getSubmarca().getId());
        vehiculo.setUso(vehiculoDto.getUso().getId());
        vehiculo.setTipo(vehiculoDto.getTipo().getId());
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

        Vehiculo vehiculoCreado = vehiculoRepository.save(vehiculo);
        return daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoCreado);
    }

    @Override
    public VehiculoDto modificarVehiculo(String vehiculoUuid, String username, VehiculoDto vehiculoDto) {
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

        vehiculo.setPlacas(vehiculoDto.getPlacas());
        vehiculo.setSerie(vehiculoDto.getSerie());
        vehiculo.setRotulado(vehiculoDto.isRotulado());
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
        vehiculo.setSubmarca(vehiculoDto.getSubmarca().getId());
        vehiculo.setUso(vehiculoDto.getUso().getId());
        vehiculo.setTipo(vehiculoDto.getTipo().getId());

        daoHelper.fulfillAuditorFields(false, vehiculo, usuarioDto.getId());

        Vehiculo vehiculoCreado = vehiculoRepository.save(vehiculo);
        return daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoCreado);
    }

    @Override
    public VehiculoDto eliminarVehiculo(String vehiculoUuid, String username) {
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

        vehiculo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, vehiculo, usuarioDto.getId());

        Vehiculo vehiculoCreado = vehiculoRepository.save(vehiculo);
        return daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoCreado);
    }
}
