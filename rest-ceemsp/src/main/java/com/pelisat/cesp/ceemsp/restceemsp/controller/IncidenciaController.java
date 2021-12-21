package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class IncidenciaController {
    private final JwtUtils jwtUtils;
    private static final String INCIDENCIA_URI = "/empresas/{empresaUuid}/incidencias";
    private final IncidenciaService incidenciaService;

    @Autowired
    public IncidenciaController(
            JwtUtils jwtUtils,
            IncidenciaService incidenciaService
    ) {
        this.jwtUtils = jwtUtils;
        this.incidenciaService = incidenciaService;
    }

    @GetMapping(value = INCIDENCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IncidenciaDto> obtenerIncidenciasPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return incidenciaService.obtenerIncidenciasPorEmpresa(empresaUuid);
    }

    @GetMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto obtenerIncidenciaPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid
    ) {
        return incidenciaService.obtenerIncidenciaPorUuid(empresaUuid, incidenciaUuid);
    }

    @PostMapping(value = INCIDENCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto guardarIncidencia(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody IncidenciaDto incidenciaDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaService.guardarIncidencia(empresaUuid, username, incidenciaDto);
    }
}
