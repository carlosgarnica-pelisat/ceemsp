package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaIncidenciaCanService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class EmpresaIncidenciaCanController {
    private final JwtUtils jwtUtils;
    private final EmpresaIncidenciaCanService empresaIncidenciaCanService;
    private static final String INCIDENCIA_CAN_URI = "/incidencias/{incidenciaUuid}/canes";

    @Autowired
    public EmpresaIncidenciaCanController(
            JwtUtils jwtUtils, EmpresaIncidenciaCanService empresaIncidenciaCanService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaIncidenciaCanService = empresaIncidenciaCanService;
    }

    @PostMapping(value = INCIDENCIA_CAN_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanDto guardarIncidenciaCan(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody CanDto canDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaCanService.agregarCanIncidencia(incidenciaUuid, username, canDto);
    }

    @DeleteMapping(value = INCIDENCIA_CAN_URI + "/{canUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanDto eliminarIncidenciaCan(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaCanService.eliminarCanIncidencia(incidenciaUuid, canUuid, username);
    }
}
