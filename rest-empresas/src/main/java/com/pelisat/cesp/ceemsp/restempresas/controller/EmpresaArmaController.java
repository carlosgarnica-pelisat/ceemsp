package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaArmaService;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaArmaServiceImpl;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaLicenciaColectivaArmaService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaArmaController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_ARMAS_URI = "/armas";
    private final EmpresaArmaService empresaArmaService;

    @Autowired
    public EmpresaArmaController(JwtUtils jwtUtils, EmpresaArmaService empresaArmaService) {
        this.jwtUtils = jwtUtils;
        this.empresaArmaService = empresaArmaService;
    }

    @GetMapping(value = EMPRESA_ARMAS_URI + "/cortas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmasCortas(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaArmaService.obtenerArmasCortasPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_ARMAS_URI + "/largas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmasLargas(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaArmaService.obtenerArmasLargasPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_ARMAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmas(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaArmaService.obtenerArmasPorEmpresa(username);
    }
}
