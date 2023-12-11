package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.ActualizarContrasenaDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.model.VehiculoFotografia;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.PasswordMismatchException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaService empresaService;
    private final DaoHelper<Usuario> daoHelper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, DaoToDtoConverter daoToDtoConverter,
                              EmpresaService empresaService, DaoHelper<Usuario> daoHelper) {
        this.usuarioRepository = usuarioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaService = empresaService;
        this.daoHelper = daoHelper;
    }

    @Override
    public UsuarioDto getUserByUsername(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El usuario esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el usuario con el email [{}]", username);
        Usuario usuario = usuarioRepository.getByUsernameAndEliminadoFalse(username);

        if(usuario == null) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        if(usuario.getEmpresa() == null) {
            logger.warn("El usuario no tiene empresa ligada. Que diantres esta haciendo aqui?");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = daoToDtoConverter.convertDaoToDtoUser(usuario);
        usuarioDto.setEmpresa(empresaService.obtenerPorId(usuario.getEmpresa()));

        return usuarioDto;
    }

    @Override
    public UsuarioDto getUserByEmail(String email) {
        if(StringUtils.isBlank(email)) {
            logger.warn("El email esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el usuario con el email [{}]", email);
        Usuario usuario = usuarioRepository.getUsuarioByEmailAndEliminadoFalse(email);

        if(usuario == null) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        if(usuario.getEmpresa() == null) {
            logger.warn("El usuario no tiene empresa ligada. Que diantres esta haciendo aqui?");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = daoToDtoConverter.convertDaoToDtoUser(usuario);
        usuarioDto.setEmpresa(empresaService.obtenerPorId(usuario.getEmpresa()));

        return usuarioDto;
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
    public File obtenerLogoEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Usuario usuario = usuarioRepository.getUsuarioByEmailAndEliminadoFalse(username);

        if(usuario == null) {
            logger.warn("El usuario no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        if(usuario.getEmpresa() == null) {
            logger.warn("El usuario no tiene empresa ligada. Que diantres esta haciendo aqui?");
            throw new NotFoundResourceException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorId(usuario.getEmpresa());
        return new File(empresaDto.getRutaLogo());
    }

    @Override
    public void actualizarContrasena(String username, ActualizarContrasenaDto actualizarContrasenaDto) {
        if(StringUtils.isBlank(username) || actualizarContrasenaDto == null) {
            logger.warn("Alguno de los campos requeridos no son validos");
            throw new InvalidDataException();
        }

        logger.info("Se esta actualizando la contrasena para el usuario [{}]", username);
        Usuario usuario = usuarioRepository.getUsuarioByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(!StringUtils.equalsIgnoreCase(usuario.getPassword(), actualizarContrasenaDto.getActualPassword())) {
            logger.warn("La contrasena no coincide con la anterior");
            throw new PasswordMismatchException();
        }

        usuario.setPassword(actualizarContrasenaDto.getPassword());
        daoHelper.fulfillAuditorFields(false, usuario, usuario.getId());
        usuarioRepository.save(usuario);
    }
}
