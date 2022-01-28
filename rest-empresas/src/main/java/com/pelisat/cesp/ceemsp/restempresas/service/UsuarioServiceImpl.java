package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
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

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaService empresaService;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, DaoToDtoConverter daoToDtoConverter,
                              EmpresaService empresaService) {
        this.usuarioRepository = usuarioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaService = empresaService;
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
        return null;
    }
}
