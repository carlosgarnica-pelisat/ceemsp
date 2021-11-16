package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaEscrituraController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_ESCRITURA_URI = "/empresas/{empresaUuid}/escrituras";
    private final EmpresaEscrituraService empresaEscrituraService;

    @Autowired
    public EmpresaEscrituraController(EmpresaEscrituraService empresaEscrituraService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaEscrituraService = empresaEscrituraService;
    }

    @GetMapping(value = EMPRESA_ESCRITURA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraDto> obtenerEscriturasPorEmpresa(
        @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaEscrituraService.obtenerEscriturasEmpresaPorUuid(empresaUuid);
    }

    @GetMapping(value = EMPRESA_ESCRITURA_URI + "/{escrituraUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraDto obtenerEscrituraPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraService.obtenerEscrituraPorUuid(empresaUuid, escrituraUuid, false);
    }

    @PostMapping(value = EMPRESA_ESCRITURA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraDto crearEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraDto empresaEscrituraDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraService.guardarEscritura(empresaUuid, empresaEscrituraDto, username);
    }
}
