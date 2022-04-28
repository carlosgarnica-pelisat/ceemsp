package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaArmaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaPersonalService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class IncidenciaArmaController {
    private final JwtUtils jwtUtils;
    private final IncidenciaArmaService incidenciaArmaService;
    private static final String INCIDENCIA_ARMA_URI = "/empresas/{empresaUuid}/incidencias/{incidenciaUuid}/armas";

    @Autowired
    public IncidenciaArmaController(
            JwtUtils jwtUtils, IncidenciaArmaService incidenciaArmaService
    ) {
        this.jwtUtils = jwtUtils;
        this.incidenciaArmaService = incidenciaArmaService;
    }

    @PostMapping(value = INCIDENCIA_ARMA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto guardarIncidenciaArma(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody ArmaDto armaDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaArmaService.agregarArmaIncidencia(empresaUuid, incidenciaUuid, username, armaDto);
    }

    @DeleteMapping(value = INCIDENCIA_ARMA_URI + "/{armaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto eliminarIncidenciaArma(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "armaUuid") String armaUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaArmaService.eliminarArmaIncidencia(empresaUuid, incidenciaUuid, armaUuid, username);
    }
}
