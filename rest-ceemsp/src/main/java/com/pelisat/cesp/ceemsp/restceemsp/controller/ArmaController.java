package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaClaseDto;
import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaClaseService;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ArmaController {
    private final ArmaService armaService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_ARMAS_URI = "/empresas/{empresaUuid}/licencias/{licenciaUuid}/armas";

    @Autowired
    public ArmaController(ArmaService armaService, JwtUtils jwtUtils) {
        this.armaService = armaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_ARMAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmas(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) {
        return armaService.obtenerArmasPorLicenciaColectivaUuid(empresaUuid, licenciaUuid);
    }

    @PostMapping(value = EMPRESA_ARMAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto guardarArma(
            HttpServletRequest request,
            @RequestBody ArmaDto armaDto,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaService.guardarArma(empresaUuid, licenciaUuid, username, armaDto);
    }
}
