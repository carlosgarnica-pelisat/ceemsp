package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanTipoAdiestramientoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CanRaza;
import com.pelisat.cesp.ceemsp.database.model.CanTipoAdiestramiento;
import com.pelisat.cesp.ceemsp.database.repository.CanTipoAdiestramientoRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
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
public class CanTipoAdiestramientoServiceImpl implements CanTipoAdiestramientoService {
    private final Logger logger = LoggerFactory.getLogger(CanTipoAdiestramientoService.class);
    private final CanTipoAdiestramientoRepository canTipoAdiestramientoRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;

    @Autowired
    public CanTipoAdiestramientoServiceImpl(CanTipoAdiestramientoRepository canTipoAdiestramientoRepository, UsuarioService usuarioService,
                                            DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter) {
        this.canTipoAdiestramientoRepository = canTipoAdiestramientoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<CanTipoAdiestramientoDto> obtenerTodos() {
        logger.info("Consultando todas las razas guardadas en la base de datos");
        List<CanTipoAdiestramiento> canTiposAdiestramiento = canTipoAdiestramientoRepository.getAllByEliminadoFalse();
        return canTiposAdiestramiento.stream()
                .map(canTipoAdiestramiento -> daoToDtoConverter.convertDaoToDtoCanTipoAdiestramiento(canTipoAdiestramiento))
                .collect(Collectors.toList());
    }

    @Override
    public CanTipoAdiestramientoDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid a consultar esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Consultando tipo de adiestramiento con el uuid [{}]", uuid);

        CanTipoAdiestramiento canTipoAdiestramiento = canTipoAdiestramientoRepository.getByUuidAndEliminadoFalse(uuid);

        if(canTipoAdiestramiento == null) {
            logger.warn("El tipo de adiestramiento no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCanTipoAdiestramiento(canTipoAdiestramiento);
    }

    @Override
    public CanTipoAdiestramientoDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id a consultar esta viniendo como invalido o nulo");
            throw new InvalidDataException();
        }

        logger.info("Consultando tipo de adiestramiento con el id [{}]", id);

        CanTipoAdiestramiento canTipoAdiestramiento = canTipoAdiestramientoRepository.getOne(id);

        if(canTipoAdiestramiento == null) {
            logger.warn("El tipo de adiestramiento no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCanTipoAdiestramiento(canTipoAdiestramiento);
    }

    @Override
    public CanTipoAdiestramientoDto crearNuevo(CanTipoAdiestramientoDto canTipoAdiestramientoDto, String username) {
        if(canTipoAdiestramientoDto == null || StringUtils.isBlank(username)) {
            logger.warn("El tipo de adiestramiento a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nuevo tipo de adiestramiento");

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        CanTipoAdiestramiento canTipoAdiestramiento = dtoToDaoConverter.convertDtoToDaoCanTipoAdiestramiento(canTipoAdiestramientoDto);

        canTipoAdiestramiento.setFechaCreacion(LocalDateTime.now());
        canTipoAdiestramiento.setCreadoPor(usuario.getId());
        canTipoAdiestramiento.setActualizadoPor(usuario.getId());
        canTipoAdiestramiento.setFechaActualizacion(LocalDateTime.now());

        CanTipoAdiestramiento tipoAdiestramientoCreado = canTipoAdiestramientoRepository.save(canTipoAdiestramiento);

        return daoToDtoConverter.convertDaoToDtoCanTipoAdiestramiento(tipoAdiestramientoCreado);
    }

    @Override
    public CanTipoAdiestramientoDto modificar(CanTipoAdiestramientoDto canTipoAdiestramientoDto, String uuid, String username) {
        return null;
    }

    @Override
    public CanTipoAdiestramientoDto eliminar(String uuid, String username) {
        return null;
    }
}
