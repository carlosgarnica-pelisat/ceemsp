package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraRepresentanteService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaFormaEjecucionService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaFormaEjecucionServiceImpl;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaFormaEjecucionController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_FORMA_EJECUCION_URI = "/empresas/{empresaUuid}/formas";
    private final EmpresaFormaEjecucionService empresaFormaEjecucionService;

    @Autowired
    public EmpresaFormaEjecucionController(EmpresaFormaEjecucionService empresaFormaEjecucionService,
                                                   JwtUtils jwtUtils) {
        this.empresaFormaEjecucionService = empresaFormaEjecucionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_FORMA_EJECUCION_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaFormaEjecucionDto> obtenerFormasEjecucionPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaFormaEjecucionService.obtenerFormasEjecucionPorEmpresaUuid(empresaUuid);
    }

    @PostMapping(value = EMPRESA_FORMA_EJECUCION_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaFormaEjecucionDto guardarFormaEjecucion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody EmpresaFormaEjecucionDto empresaFormaEjecucionDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaFormaEjecucionService.crearFormaEjecucion(empresaUuid, username, empresaFormaEjecucionDto);
    }
}
