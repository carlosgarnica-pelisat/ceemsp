package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionArgosDto;
import com.pelisat.cesp.ceemsp.database.dto.NotificacionEmpresaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.NotificacionEmpresaService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NotificacionEmpresaController {
    private final JwtUtils jwtUtils;
    private final NotificacionEmpresaService notificacionEmpresaService;
    private static final String NOTIFICACION_ARGOS_URI = "/notificaciones-empresas";

    @Autowired
    public NotificacionEmpresaController(JwtUtils jwtUtils, NotificacionEmpresaService notificacionEmpresaService) {
        this.notificacionEmpresaService = notificacionEmpresaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = NOTIFICACION_ARGOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NotificacionEmpresaDto> obtenerNotificacionesArgos(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return notificacionEmpresaService.obtenerNotificacionesPorUsuario(username);
    }

    @PutMapping(value = NOTIFICACION_ARGOS_URI + "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public NotificacionEmpresaDto leerNotificacionArgos(
            @PathVariable(value = "uuid") String uuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return notificacionEmpresaService.leerNotificacion(uuid, username);
    }
}
