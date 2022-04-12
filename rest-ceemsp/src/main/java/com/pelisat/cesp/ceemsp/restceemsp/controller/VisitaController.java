package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoUsoDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.UniformeService;
import com.pelisat.cesp.ceemsp.restceemsp.service.VisitaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VisitaController {
    private final VisitaService visitaService;
    private final JwtUtils jwtUtils;
    private static final String VISITA_URI = "/visitas";

    @Autowired
    public VisitaController(VisitaService visitaService, JwtUtils jwtUtils) {
        this.visitaService = visitaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VISITA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VisitaDto> obtenerVisitas() {
        return visitaService.obtenerTodas();
    }

    @GetMapping(value = VISITA_URI + "/proximas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VisitaDto> obtenerProximasVisitas() {
        return visitaService.obtenerProximasVisitas();
    }

    @GetMapping(value = VISITA_URI + "/{visitaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VisitaDto obtenerVisitaPorUuid(
            @PathVariable(value = "visitaUuid") String visitaUuid
    ) {
        return visitaService.obtenerPorUuid(visitaUuid);
    }

    @PostMapping(value = VISITA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VisitaDto guardarVisita(
            HttpServletRequest request,
            @RequestBody VisitaDto visitaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return visitaService.crearNuevo(visitaDto, username);
    }

    @PutMapping(value = VISITA_URI + "/{visitaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VisitaDto modificarVisita(
            HttpServletRequest request,
            @RequestBody VisitaDto visitaDto,
            @PathVariable(value = "visitaUuid") String visitaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return visitaService.modificarVisita(visitaUuid, username, visitaDto);
    }

    @PutMapping(value = VISITA_URI + "/{visitaUuid}/requerimientos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VisitaDto modificarRequerimientoVisita(
            HttpServletRequest request,
            @RequestBody VisitaDto visitaDto,
            @PathVariable(value = "visitaUuid") String visitaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return visitaService.modificarRequerimiento(visitaUuid, username, visitaDto);
    }

    @DeleteMapping(value = VISITA_URI + "/{visitaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VisitaDto modificarVisita(
            HttpServletRequest request,
            @PathVariable(value = "visitaUuid") String visitaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return visitaService.eliminarVisita(visitaUuid, username);
    }
}
