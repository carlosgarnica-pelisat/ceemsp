package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaArmaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaCanService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class IncidenciaCanController {
    private final JwtUtils jwtUtils;
    private final IncidenciaCanService incidenciaCanService;
    private static final String INCIDENCIA_CAN_URI = "/empresas/{empresaUuid}/incidencias/{incidenciaUuid}/canes";

    @Autowired
    public IncidenciaCanController(
            JwtUtils jwtUtils, IncidenciaCanService incidenciaCanService
    ) {
        this.jwtUtils = jwtUtils;
        this.incidenciaCanService = incidenciaCanService;
    }

    @PostMapping(value = INCIDENCIA_CAN_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanDto guardarIncidenciaCan(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody CanDto canDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaCanService.agregarCanIncidencia(empresaUuid, incidenciaUuid, username, canDto);
    }

    @DeleteMapping(value = INCIDENCIA_CAN_URI + "/{canUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanDto eliminarIncidenciaCan(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaCanService.eliminarCanIncidencia(empresaUuid, incidenciaUuid, canUuid, username);
    }
}
