package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaIncidenciaArmaService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class EmpresaIncidenciaArmaController {
    private final JwtUtils jwtUtils;
    private final EmpresaIncidenciaArmaService empresaIncidenciaArmaService;
    private static final String INCIDENCIA_ARMA_URI = "/incidencias/{incidenciaUuid}/armas";

    @Autowired
    public EmpresaIncidenciaArmaController(
            JwtUtils jwtUtils, EmpresaIncidenciaArmaService empresaIncidenciaArmaService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaIncidenciaArmaService = empresaIncidenciaArmaService;
    }

    @PostMapping(value = INCIDENCIA_ARMA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto guardarIncidenciaArma(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody ArmaDto armaDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaArmaService.agregarArmaIncidencia(incidenciaUuid, username, armaDto);
    }

    @PutMapping(value = INCIDENCIA_ARMA_URI + "/{armaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto eliminarIncidenciaArma(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "armaUuid") String armaUuid,
            @RequestBody ArmaDto armaDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaArmaService.eliminarArmaIncidencia(incidenciaUuid, armaUuid, username, armaDto);
    }
}
