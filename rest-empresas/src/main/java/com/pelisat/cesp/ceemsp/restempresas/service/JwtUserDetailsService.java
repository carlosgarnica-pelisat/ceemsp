package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.database.type.RolTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Autowired
    public JwtUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.getUsuarioByEmail(email);
        if(usuario == null) {
            logger.warn("El usuario [{}] no existe en la base de datos", email);
            throw new UsernameNotFoundException("The user was not found on the database: " + email);
        }

        if(usuario.getRol() != RolTypeEnum.ENTERPRISE_USER) {
            logger.warn("El usuario [{}] no tiene el rol para ingresar aqui", email);
            throw new UsernameNotFoundException("The user does not have enough privileges");
        }

        return new User(usuario.getEmail(), usuario.getPassword(), new ArrayList<>());
    }
}
