package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoTipoDto;
import com.pelisat.cesp.ceemsp.database.model.ArmaClase;
import com.pelisat.cesp.ceemsp.database.model.ArmaMarca;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoTipoRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculoTipoServiceImpl implements VehiculoTipoService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final VehiculoTipoRepository vehiculoTipoRepository;
    private final UsuarioService usuarioService;
    private final Logger logger = LoggerFactory.getLogger(VehiculoTipoService.class);

    @Autowired
    public VehiculoTipoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                   DaoHelper<CommonModel> daoHelper, VehiculoTipoRepository vehiculoTipoRepository,
                                   UsuarioService usuarioService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.vehiculoTipoRepository = vehiculoTipoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<VehiculoTipoDto> obtenerTodos() {
        logger.info("Consultando todos los tipos de vehiculos guardadas en la base de datos");
        List<VehiculoTipo> vehiculoTipos = vehiculoTipoRepository.getAllByEliminadoFalse();
        return vehiculoTipos.stream()
                .map(vehiculoTipo -> daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipo))
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoTipoDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando el tipo de vehiculo con el uuid [{}]", uuid);

        VehiculoTipo vehiculoTipo = vehiculoTipoRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoTipo == null) {
            logger.warn("El tipo de vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipo);
    }

    @Override
    public VehiculoTipoDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando el tipo de vehiculo con el id [{}]", id);

        VehiculoTipo vehiculoTipo = vehiculoTipoRepository.getOne(id);

        if(vehiculoTipo == null) {
            logger.warn("El armna clase no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipo);
    }

    @Override
    public VehiculoTipoDto crearNuevo(VehiculoTipoDto vehiculoTipoDto, String username) {
        if(vehiculoTipoDto == null || StringUtils.isBlank(username)) {
            logger.warn("El usuario o el tipo de vehiculo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando tipo de vehiculo con nombre: [{}]", vehiculoTipoDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        VehiculoTipo vehiculoTipo = dtoToDaoConverter.convertDtoToDaoVehiculoTipo(vehiculoTipoDto);
        daoHelper.fulfillAuditorFields(true, vehiculoTipo, usuario.getId());
        VehiculoTipo vehiculoTipoCreado = vehiculoTipoRepository.save(vehiculoTipo);

        return daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipoCreado);
    }

    @Override
    public VehiculoTipoDto modificar(VehiculoTipoDto vehiculoTipoDto, String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || vehiculoTipoDto == null) {
            logger.warn("Alguno de los campos vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el tipo del vehiculo con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        VehiculoTipo vehiculoTipo = vehiculoTipoRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoTipo == null) {
            logger.warn("La marca no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        vehiculoTipo.setNombre(vehiculoTipoDto.getNombre());
        vehiculoTipo.setDescripcion(vehiculoTipoDto.getDescripcion());
        vehiculoTipo.setFechaActualizacion(LocalDateTime.now());
        vehiculoTipo.setActualizadoPor(usuario.getId());

        vehiculoTipoRepository.save(vehiculoTipo);

        return daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipo);
    }

    @Override
    public VehiculoTipoDto eliminar(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la marca del arma con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        VehiculoTipo vehiculoTipo = vehiculoTipoRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoTipo == null) {
            logger.warn("El tipo del vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        vehiculoTipo.setEliminado(true);
        vehiculoTipo.setFechaActualizacion(LocalDateTime.now());
        vehiculoTipo.setActualizadoPor(usuario.getId());

        vehiculoTipoRepository.save(vehiculoTipo);

        return daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipo);
    }
}
