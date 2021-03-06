package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaController {
    private final EmpresaService empresaService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESAS_URI = "/empresas";

    @Autowired
    public EmpresaController(EmpresaService empresaService, JwtUtils jwtUtils) {
        this.empresaService = empresaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESAS_URI)
    public List<EmpresaDto> obtenerEmpresas() {
        return empresaService.obtenerTodas();
    }

    @PostMapping(value = EMPRESAS_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDto guardarEmpresa(
            @RequestBody EmpresaDto empresaDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaService.crearEmpresa(empresaDto, username);
    }

    @GetMapping(value = EMPRESAS_URI + "/{empresaUuid}")
    public EmpresaDto obtenerEmpresaPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaService.obtenerPorUuid(empresaUuid);
    }

    @PutMapping(value = EMPRESAS_URI + "/{empresaUuid}")
    public EmpresaDto modificarDatosEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            HttpServletRequest request,
            @RequestBody EmpresaDto empresaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaService.modificarEmpresa(empresaDto, username, empresaUuid);
    }

    @PutMapping(value = EMPRESAS_URI + "/{empresaUuid}/status")
    public EmpresaDto cambiarStatusEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            HttpServletRequest request,
            @RequestBody EmpresaDto empresaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaService.cambiarStatusEmpresa(empresaDto, username, empresaUuid);
    }
}
