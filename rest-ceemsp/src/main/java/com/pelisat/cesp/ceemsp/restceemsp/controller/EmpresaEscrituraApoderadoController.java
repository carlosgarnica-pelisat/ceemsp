package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraApoderadoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaEscrituraApoderadoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_APODERADOS_URI = "/empresas/{empresaUuid}/escrituras/{escrituraUuid}/apoderados";
    private final EmpresaEscrituraApoderadoService empresaEscrituraApoderadoService;

    @Autowired
    public EmpresaEscrituraApoderadoController(EmpresaEscrituraApoderadoService empresaEscrituraApoderadoService,
                                           JwtUtils jwtUtils) {
        this.empresaEscrituraApoderadoService = empresaEscrituraApoderadoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_APODERADOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraApoderadoService.obtenerApoderadosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_APODERADOS_URI + "/{socioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto obtenerEscrituraApoderadoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String socioUuid
    ) {
        return null;
        //return empresaEscrituraApoderadoService.obtenerRepresentantePorUuid(empresaUuid, escrituraUuid, socioUuid, false);
    }

    @PostMapping(value = EMPRESA_APODERADOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto crearApoderado(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.crearApoderado(empresaUuid, escrituraUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_APODERADOS_URI + "/{apoderadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto modificarApoderado(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String apoderadoUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.modificarApoderado(empresaUuid, escrituraUuid, apoderadoUuid, username, empresaEscrituraApoderadoDto);
    }

    @DeleteMapping(value = EMPRESA_APODERADOS_URI + "/{apoderadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto eliminarApoderado(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String apoderadoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.eliminarApoderado(empresaUuid, escrituraUuid, apoderadoUuid, username);
    }
}
