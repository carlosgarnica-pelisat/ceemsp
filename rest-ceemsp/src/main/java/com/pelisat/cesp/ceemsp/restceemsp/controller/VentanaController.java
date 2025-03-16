package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VentanaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.VentanaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.VentanaServiceImpl;
import com.pelisat.cesp.ceemsp.restceemsp.service.VisitaArchivoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VentanaController {
    private final VentanaService ventanaService;
    private final JwtUtils jwtUtils;
    private static final String VENTANA_URI = "/ventanas";

    @Autowired
    public VentanaController(VentanaService ventanaService, JwtUtils jwtUtils) {
        this.ventanaService = ventanaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VENTANA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VentanaDto> obtenerVentanas() {
        return ventanaService.obtenerVentanas();
    }

    @GetMapping(value = VENTANA_URI + "/{ventanaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VentanaDto obtenerVentanaPorUuid(
            @PathVariable(value = "ventanaUuid") String uuid
    ) {
        return ventanaService.obtenerVentanaPorUuid(uuid);
    }

    @PostMapping(value = VENTANA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VentanaDto guardarVentana(
            HttpServletRequest request,
            @RequestBody VentanaDto ventanaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return ventanaService.guardarVentana(ventanaDto, username);
    }

    @PutMapping(value = VENTANA_URI + "/{ventanaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VentanaDto modificarVentana(
            HttpServletRequest request,
            @RequestBody VentanaDto ventanaDto,
            @PathVariable(value = "ventanaUuid") String uuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return ventanaService.modificarVentana(uuid, ventanaDto, username);
    }

    @DeleteMapping(value = VENTANA_URI + "/{ventanaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VentanaDto eliminarVentana(
            HttpServletRequest request,
            @PathVariable(value = "ventanaUuid") String uuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return ventanaService.eliminarVentana(uuid, username);
    }
}
