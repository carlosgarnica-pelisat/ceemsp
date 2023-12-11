package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionCorreoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.NotificacionesCorreoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class NotificacionesCorreoController {
    private final JwtUtils jwtUtils;
    private static final String NOTIFICACION_EMAIL_URI = "/notificaciones-email";
    private final NotificacionesCorreoService notificacionesCorreoService;

    @Autowired
    public NotificacionesCorreoController(JwtUtils jwtUtils, NotificacionesCorreoService notificacionesCorreoService) {
        this.jwtUtils = jwtUtils;
        this.notificacionesCorreoService = notificacionesCorreoService;
    }

    @PostMapping(value = NOTIFICACION_EMAIL_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void generarNotificacionCorreo(
            HttpServletRequest request,
            @RequestBody NotificacionCorreoDto notificacion
            ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        notificacionesCorreoService.generarCorreoElectronico(username, notificacion);
    }
}
