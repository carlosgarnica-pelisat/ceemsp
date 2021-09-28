package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private final UsuarioRepository usuarioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter) {
        this.usuarioRepository = usuarioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
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
    public UsuarioDto saveUser(UsuarioDto userDto, String username) {
        return null;
    }

    @Override
    public UsuarioDto getUserByUuid(String uuid) {
        return null;
    }

    @Override
    public UsuarioDto getUserByUsername(String username) {
        return null;
    }

    @Override
    public UsuarioDto getUserById(int id) {
        return null;
    }

    @Override
    public UsuarioDto updateUserByUuid(String uuid, UsuarioDto userDto, String username) {
        return null;
    }

    @Override
    public UsuarioDto deleteUser(String uuid, String username) {
        return null;
    }
}
