package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionArgosDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.NotificacionArgosService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NotificacionArgosController {
    private final JwtUtils jwtUtils;
    private final NotificacionArgosService notificacionArgosService;
    private static final String NOTIFICACION_ARGOS_URI = "/notificaciones-argos";

    @Autowired
    public NotificacionArgosController(JwtUtils jwtUtils, NotificacionArgosService notificacionArgosService) {
        this.notificacionArgosService = notificacionArgosService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = NOTIFICACION_ARGOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NotificacionArgosDto> obtenerNotificacionesArgos(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return notificacionArgosService.obtenerNotificacionesPorUsuario(username);
    }

    @PutMapping(value = NOTIFICACION_ARGOS_URI + "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public NotificacionArgosDto leerNotificacionArgos(
            @PathVariable(value = "uuid") String uuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return notificacionArgosService.leerNotificacion(uuid, username);
    }
}
