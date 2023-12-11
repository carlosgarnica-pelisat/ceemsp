package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.ActualizarContrasenaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.UsuarioService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/v1")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;
    private static final String USUARIO_URI = "/usuarios";

    @Autowired
    public UsuarioController(UsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = USUARIO_URI + "/perfil", produces = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto obtenerPerfil(
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return usuarioService.getUserByEmail(username);
    }

    @GetMapping(value = USUARIO_URI + "/logo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarFotografiaVehiculo(
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        File file = usuarioService.obtenerLogoEmpresa(username);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG); // TODO: Validar el tipo de imagen en funcion de su formato
        httpHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = USUARIO_URI + "/cambio-contrasena", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void actualizarContrasena(
            HttpServletRequest httpServletRequest,
            @RequestBody ActualizarContrasenaDto actualizarContrasenaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        usuarioService.actualizarContrasena(username, actualizarContrasenaDto);
    }
}
