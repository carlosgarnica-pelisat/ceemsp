package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.database.type.RolTypeEnum;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private final UsuarioRepository usuarioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper) {
        this.usuarioRepository = usuarioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<UsuarioDto> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        List<Usuario> usuarios = usuarioRepository.findAllByEliminadoFalse();

        if(usuarios.size() == 0) {
            logger.warn("No hay usuarios creados ahora");
        }

        return usuarios.stream()
                .map(user -> daoToDtoConverter.convertDaoToDtoUser(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDto> obtenerUsuariosInternos() {
        logger.info("Obteniendo los usuarios internos");
        List<Usuario> usuarios = usuarioRepository.findAllByRolInAndEliminadoFalse(Arrays.asList(RolTypeEnum.CEEMSP_USER, RolTypeEnum.CEEMSP_SUPERUSER));

        return usuarios.stream()
                .map(user -> daoToDtoConverter.convertDaoToDtoUser(user))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto saveUser(UsuarioDto userDto, String username) {
        if(userDto == null || StringUtils.isBlank(username)) {
            logger.warn("El usuario o el nombre de usuario vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando un nuevo usuario");

        UsuarioDto usuarioCreador = getUserByEmail(username);
        Usuario usuario = dtoToDaoConverter.convertDtoToDaoUser(userDto);
        daoHelper.fulfillAuditorFields(true, usuario, usuarioCreador.getId());

        Usuario usuarioCreado = usuarioRepository.save(usuario);

        return daoToDtoConverter.convertDaoToDtoUser(usuarioCreado);
    }

    @Override
    public UsuarioDto getUserByUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el usuario con el uuid [{}]", uuid);
        Usuario usuario = usuarioRepository.getUsuarioByUuidAndEliminadoFalse(uuid);

        if(usuario == null) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoUser(usuario);
    }

    @Override
    public UsuarioDto getUserByUsername(String username) {
        return null;
    }

    @Override
    public UsuarioDto getUserByEmail(String email) {
        if(StringUtils.isBlank(email)) {
            logger.warn("El email esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el usuario con el email [{}]", email);
        Usuario usuario = usuarioRepository.getUsuarioByEmail(email);

        if(usuario == null) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoUser(usuario);
    }

    @Override
    public UsuarioDto getUserById(int id) {
        if(id < 1) {
            logger.warn("El id esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el usuario con el uuid [{}]", id);
        Usuario usuario = usuarioRepository.getOne(id);

        if(usuario == null || usuario.getEliminado()) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoUser(usuario);
    }

    @Override
    public UsuarioDto updateUserByUuid(String uuid, UsuarioDto userDto, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || userDto == null) {
            logger.warn("El email esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Actualizando el usuario con el uuid [{}]", uuid);
        Usuario usuario = usuarioRepository.getUsuarioByUuidAndEliminadoFalse(uuid);

        if(usuario == null) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioQueModifico = getUserByEmail(username);

        usuario.setNombres(userDto.getNombres());
        usuario.setApellidos(userDto.getApellidos());
        usuario.setRol(userDto.getRol());
        usuario.setPassword(userDto.getPassword());
        usuario.setUsername(userDto.getUsername());
        usuario.setEmail(userDto.getEmail());

        daoHelper.fulfillAuditorFields(false, usuario, usuarioQueModifico.getId());

        usuarioRepository.save(usuario);
        return daoToDtoConverter.convertDaoToDtoUser(usuario);
    }

    @Override
    public UsuarioDto deleteUser(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("El email esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el usuario con el uuid [{}]", uuid);
        Usuario usuario = usuarioRepository.getUsuarioByUuidAndEliminadoFalse(uuid);

        if(usuario == null) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioQueModifico = getUserByEmail(username);

        usuario.setEliminado(true);

        daoHelper.fulfillAuditorFields(false, usuario, usuarioQueModifico.getId());

        usuarioRepository.save(usuario);
        return daoToDtoConverter.convertDaoToDtoUser(usuario);
    }
}
