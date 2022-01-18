package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaDomicilioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaDomicilioController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_DOMICILIOS_URI = "/empresas/{empresaUuid}/domicilios";
    private final EmpresaDomicilioService empresaDomicilioService;

    @Autowired
    public EmpresaDomicilioController(EmpresaDomicilioService empresaDomicilioService,
                                      JwtUtils jwtUtils) {
        this.empresaDomicilioService = empresaDomicilioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_DOMICILIOS_URI)
    public List<EmpresaDomicilioDto> obtenerDomiciliosPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaDomicilioService.obtenerPorEmpresaUuid(empresaUuid);
    }

    @GetMapping(value = EMPRESA_DOMICILIOS_URI + "/{domicilioUuid}")
    public EmpresaDomicilioDto obtenerDomicilioPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) {
        return empresaDomicilioService.obtenerPorUuid(empresaUuid, domicilioUuid);
    }

    @PostMapping(value = EMPRESA_DOMICILIOS_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioDto guardarDomicilioEmpresa(
            @RequestBody EmpresaDomicilioDto empresaDomicilioDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaDomicilioService.guardar(empresaUuid, username, empresaDomicilioDto);
    }

    @PutMapping(value = EMPRESA_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioDto modificarEmpresaDomicilio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid,
            @RequestBody EmpresaDomicilioDto empresaDomicilioDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaDomicilioService.modificarEmpresaDomicilio(empresaUuid, domicilioUuid, username, empresaDomicilioDto);
    }

    @DeleteMapping(value = EMPRESA_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioDto eliminarEmpresaDomicilio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaDomicilioService.eliminarEmpresaDomicilio(empresaUuid, domicilioUuid, username);
    }
}
