package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraRepresentanteService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraSocioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaEscrituraRepresentanteController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_REPRESENTANTES_URI = "/empresas/{empresaUuid}/escrituras/{escrituraUuid}/representantes";
    private final EmpresaEscrituraRepresentanteService empresaEscrituraRepresentanteService;

    @Autowired
    public EmpresaEscrituraRepresentanteController(EmpresaEscrituraRepresentanteService empresaEscrituraRepresentanteService,
                                           JwtUtils jwtUtils) {
        this.empresaEscrituraRepresentanteService = empresaEscrituraRepresentanteService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_REPRESENTANTES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraRepresentanteDto> obtenerRepresentantesPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraRepresentanteService.obtenerRepresentantesPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_REPRESENTANTES_URI + "/{representanteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraRepresentanteDto obtenerEscrituraRepresentantePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "representanteUuid") String representanteUuid
    ) {
        return null;
        //return empresaEscrituraRepresentanteService.obtenerRepresentantePorUuid(empresaUuid, escrituraUuid, representanteUuid, false);
    }

    @PostMapping(value = EMPRESA_REPRESENTANTES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraRepresentanteDto crearEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraRepresentanteService.crearRepresentante(empresaUuid, escrituraUuid, username, empresaEscrituraRepresentanteDto);
    }
}
