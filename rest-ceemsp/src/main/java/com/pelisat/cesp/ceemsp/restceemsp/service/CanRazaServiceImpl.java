package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanRazaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CanRaza;
import com.pelisat.cesp.ceemsp.database.repository.CanRazaRepository;
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
public class CanRazaServiceImpl implements CanRazaService {

    private final CanRazaRepository canRazaRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final Logger logger = LoggerFactory.getLogger(CanRazaServiceImpl.class);

    @Autowired
    public CanRazaServiceImpl(CanRazaRepository canRazaRepository, UsuarioService usuarioService,
                              DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter) {
        this.canRazaRepository = canRazaRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<CanRazaDto> obtenerTodos() {
        logger.info("Consultando todas las razas guardadas en la base de datos");
        List<CanRaza> canRazas = canRazaRepository.getAllByEliminadoFalse();
        return canRazas.stream()
                .map(canRaza -> daoToDtoConverter.convertDaoToDtoCanRaza(canRaza))
                .collect(Collectors.toList());
    }

    @Override
    public CanRazaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El usuario o el uuid estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Consultando Raza con el uuid [{}]", uuid);

        CanRaza canRaza = canRazaRepository.getByUuidAndEliminadoFalse(uuid);

        if(canRaza == null) {
            logger.warn("La raza no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCanRaza(canRaza);
    }

    @Override
    public CanRazaDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando Raza con el id [{}]", id);

        CanRaza canRaza = canRazaRepository.getOne(id);

        if(canRaza == null) {
            logger.warn("La raza no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCanRaza(canRaza);
    }

    @Override
    public CanRazaDto crearNuevo(CanRazaDto canRazaDto, String username) {
        if(canRazaDto == null || StringUtils.isBlank(username)) {
            logger.warn("El usuario o la raza a ser creada vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nueva raza");

        CanRaza canRaza = dtoToDaoConverter.convertDtoToDaoCanRaza(canRazaDto);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        canRaza.setFechaCreacion(LocalDateTime.now());
        canRaza.setCreadoPor(usuario.getId());
        canRaza.setActualizadoPor(usuario.getId());
        canRaza.setFechaActualizacion(LocalDateTime.now());

        CanRaza canRazaCreado = canRazaRepository.save(canRaza);

        return daoToDtoConverter.convertDaoToDtoCanRaza(canRazaCreado);
    }

    @Override
    public CanRazaDto modificar(CanRazaDto canRazaDto, String uuid, String username) {
        return null;
    }

    @Override
    public CanRazaDto eliminar(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("El usuario o el uuid estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Eliminando Can Raza con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByUsername(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        CanRaza canRaza = canRazaRepository.getByUuidAndEliminadoFalse(uuid);

        if(canRaza == null) {
            logger.warn("La raza no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        canRaza.setEliminado(true);
        canRaza.setFechaActualizacion(LocalDateTime.now());
        canRaza.setActualizadoPor(usuario.getId());

        canRazaRepository.save(canRaza);

        return daoToDtoConverter.convertDaoToDtoCanRaza(canRaza);
    }
}
