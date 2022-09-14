package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaPersonalService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class IncidenciaPersonalController {
    private final JwtUtils jwtUtils;
    private final IncidenciaPersonalService incidenciaPersonalService;
    private static final String INCIDENCIA_PERSONAL_URI = "/empresas/{empresaUuid}/incidencias/{incidenciaUuid}/personal";

    @Autowired
    public IncidenciaPersonalController(
            JwtUtils jwtUtils, IncidenciaPersonalService incidenciaPersonalService
    ) {
        this.jwtUtils = jwtUtils;
        this.incidenciaPersonalService = incidenciaPersonalService;
    }

    @GetMapping(value = INCIDENCIA_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerPersonaIncidencias(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid
    ) {
        return incidenciaPersonalService.obtenerPersonasIncidencia(empresaUuid, incidenciaUuid);
    }

    @PostMapping(value = INCIDENCIA_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto guardarIncidenciaPersona(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody PersonaDto personaDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaPersonalService.agregarPersonaIncidencia(empresaUuid, incidenciaUuid, username, personaDto);
    }

    @DeleteMapping(value = INCIDENCIA_PERSONAL_URI + "/{personaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto eliminarIncidenciaPersona(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "personaUuid") String personaUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaPersonalService.eliminarPersonaIncidencia(empresaUuid, incidenciaUuid, personaUuid, username);
    }
}
