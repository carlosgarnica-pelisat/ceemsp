package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.model.ActualizarContrasenaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.UsuarioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;
    private static final String PERFIL_URI = "/perfil";

    @Autowired
    public PerfilController(UsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
    }
    @PostMapping(value = PERFIL_URI + "/contrasena", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void actualizarContrasena(
            HttpServletRequest httpServletRequest,
            @RequestBody ActualizarContrasenaDto actualizarContrasenaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        usuarioService.actualizarContrasena(username, actualizarContrasenaDto);
    }
}
