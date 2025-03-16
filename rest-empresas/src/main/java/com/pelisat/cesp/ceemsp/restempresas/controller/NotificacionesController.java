package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.NotificacionesService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NotificacionesController {
    private final JwtUtils jwtUtils;
    private final NotificacionesService notificacionesService;
    private static final String NOTIFICACIONES_URI = "/notificaciones";

    @Autowired
    public NotificacionesController(JwtUtils jwtUtils, NotificacionesService notificacionesService) {
        this.jwtUtils = jwtUtils;
        this.notificacionesService = notificacionesService;
    }

    @GetMapping(value = NOTIFICACIONES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BuzonInternoDto> obtenerNotificaciones(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return notificacionesService.obtenerNotificacionesPorEmpresa(username);
    }

    @GetMapping(value = NOTIFICACIONES_URI + "/{notificacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDto leerNotificacion(
            HttpServletRequest request,
            @PathVariable("notificacionUuid") String notificacionUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return notificacionesService.leerNotificacion(username, notificacionUuid);
    }
}
