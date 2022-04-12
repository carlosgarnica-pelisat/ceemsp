package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoUsoDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import com.pelisat.cesp.ceemsp.database.model.VehiculoUso;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoUsoRepository;
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
public class VehiculoUsoServiceImpl implements VehiculoUsoService {

    private Logger logger = LoggerFactory.getLogger(VehiculoUsoService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final VehiculoUsoRepository vehiculoUsoRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public VehiculoUsoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                   DaoHelper<CommonModel> daoHelper, VehiculoUsoRepository vehiculoUsoRepository,
                                   UsuarioService usuarioService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.vehiculoUsoRepository = vehiculoUsoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<VehiculoUsoDto> obtenerTodos() {
        logger.info("Consultando todos los usos de vehiculos guardadas en la base de datos");
        List<VehiculoUso> vehiculoUsos = vehiculoUsoRepository.getAllByEliminadoFalse();
        return vehiculoUsos.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculoUso)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoUsoDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando el uso del vehiculo con el uuid [{}]", uuid);

        VehiculoUso vehiculoUso = vehiculoUsoRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoUso == null) {
            logger.warn("El uso de vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoUso(vehiculoUso);
    }

    @Override
    public VehiculoUsoDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando el tipo de vehiculo con el id [{}]", id);

        VehiculoUso vehiculoUso = vehiculoUsoRepository.getOne(id);

        if(vehiculoUso == null) {
            logger.warn("El uso no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoUso(vehiculoUso);
    }

    @Override
    public VehiculoUsoDto crearNuevo(VehiculoUsoDto vehiculoUsoDto, String username) {
        if(vehiculoUsoDto == null || StringUtils.isBlank(username)) {
            logger.warn("El usuario o el uso de vehiculo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando uso de vehiculo con nombre: [{}]", vehiculoUsoDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        VehiculoUso vehiculoUso = dtoToDaoConverter.convertDtoToDaoVehiculoUso(vehiculoUsoDto);
        daoHelper.fulfillAuditorFields(true, vehiculoUso, usuario.getId());
        VehiculoUso vehiculoUsoCreado = vehiculoUsoRepository.save(vehiculoUso);

        return daoToDtoConverter.convertDaoToDtoVehiculoUso(vehiculoUsoCreado);
    }

    @Override
    public VehiculoUsoDto modificar(VehiculoUsoDto vehiculoUsoDto, String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || vehiculoUsoDto == null) {
            logger.warn("Alguno de los campos vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el uso del vehiculo con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        VehiculoUso vehiculoUso = vehiculoUsoRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoUso == null) {
            logger.warn("El uso del vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        vehiculoUso.setNombre(vehiculoUsoDto.getNombre());
        vehiculoUso.setDescripcion(vehiculoUsoDto.getDescripcion());
        vehiculoUso.setFechaActualizacion(LocalDateTime.now());
        vehiculoUso.setActualizadoPor(usuario.getId());

        vehiculoUsoRepository.save(vehiculoUso);

        return daoToDtoConverter.convertDaoToDtoVehiculoUso(vehiculoUso);
    }

    @Override
    public VehiculoUsoDto eliminar(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la marca del arma con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        VehiculoUso vehiculoUso = vehiculoUsoRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoUso == null) {
            logger.warn("El tipo del vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        vehiculoUso.setEliminado(true);
        vehiculoUso.setFechaActualizacion(LocalDateTime.now());
        vehiculoUso.setActualizadoPor(usuario.getId());

        vehiculoUsoRepository.save(vehiculoUso);

        return daoToDtoConverter.convertDaoToDtoVehiculoUso(vehiculoUso);
    }
}
