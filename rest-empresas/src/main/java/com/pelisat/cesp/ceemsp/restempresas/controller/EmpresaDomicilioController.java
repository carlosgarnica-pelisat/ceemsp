package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaDomicilioService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaDomicilioController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_DOMICILIOS_URI = "/domicilios";
    private final EmpresaDomicilioService empresaDomicilioService;

    @Autowired
    public EmpresaDomicilioController(
            JwtUtils jwtUtils,
            EmpresaDomicilioService empresaDomicilioService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaDomicilioService = empresaDomicilioService;
    }

    @GetMapping(value = EMPRESA_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaDomicilioDto> obtenerDomiciliosPorEmpresa(
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaDomicilioService.obtenerDomicilios(username);
    }

    @GetMapping(value = EMPRESA_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioDto obtenerDomicilioPorUuid(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaDomicilioService.obtenerDomicilioPorUuid(username, domicilioUuid);
    }

    @PostMapping(value = EMPRESA_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioDto guardarDomicilio(
            @RequestBody EmpresaDomicilioDto empresaDomicilioDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaDomicilioService.guardarDomicilio(username, empresaDomicilioDto);
    }
}
