package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaIncidenciaPersonalService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class EmpresaIncidenciaPersonalController {
    private final JwtUtils jwtUtils;
    private final EmpresaIncidenciaPersonalService incidenciaPersonalService;
    private static final String INCIDENCIA_PERSONAL_URI = "/incidencias/{incidenciaUuid}/personal";

    @Autowired
    public EmpresaIncidenciaPersonalController(
            JwtUtils jwtUtils, EmpresaIncidenciaPersonalService incidenciaPersonalService
    ) {
        this.jwtUtils = jwtUtils;
        this.incidenciaPersonalService = incidenciaPersonalService;
    }

    @PostMapping(value = INCIDENCIA_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto guardarIncidencia(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody PersonaDto personaDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaPersonalService.agregarPersonaIncidencia(incidenciaUuid, username, personaDto);
    }

    @DeleteMapping(value = INCIDENCIA_PERSONAL_URI + "/{personaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto eliminarIncidencia(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "personaUuid") String personaUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaPersonalService.eliminarPersonaIncidencia(incidenciaUuid, personaUuid, username);
    }
}
