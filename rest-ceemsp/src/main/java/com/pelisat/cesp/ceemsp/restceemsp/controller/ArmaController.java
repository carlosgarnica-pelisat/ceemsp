package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.ArmaClaseDto;
import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaClaseService;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping(value = "/empresas/{empresaUuid}/armas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmasPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return armaService.obtenerArmasPorEmpresaUuid(empresaUuid);
    }

    @GetMapping(value = "/empresas/{empresaUuid}/armas/cortas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmasCortas(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return armaService.obtenerArmasCortasPorEmpresaUuid(empresaUuid);
    }

    @GetMapping(value = "/empresas/{empresaUuid}/armas/largas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmasLargas(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return armaService.obtenerArmasLargasPorEmpresaUuid(empresaUuid);
    }

    @GetMapping(value = EMPRESA_ARMAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmas(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) {
        return armaService.obtenerArmasPorLicenciaColectivaUuid(empresaUuid, licenciaUuid);
    }

    @GetMapping(value = EMPRESA_ARMAS_URI + "/todas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerTodasArmas(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) {
        return armaService.obtenerTodasArmasPorLicenciaColectivaUuid(empresaUuid, licenciaUuid);
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

    @PutMapping(value = EMPRESA_ARMAS_URI + "/{armaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto modificarArma(
            HttpServletRequest request,
            @RequestBody ArmaDto armaDto,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            @PathVariable(value = "armaUuid") String armaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaService.modificarArma(empresaUuid, licenciaUuid, armaUuid, username, armaDto);
    }

    @PutMapping(value = EMPRESA_ARMAS_URI + "/{armaUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto eliminarArma(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            @PathVariable(value = "armaUuid") String armaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("arma") String arma
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaService.eliminarArma(empresaUuid, licenciaUuid, armaUuid, username, new Gson().fromJson(arma, ArmaDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_ARMAS_URI + "/{armaUuid}/custodia", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArmaDto cambiarStatusArma(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            @PathVariable(value = "armaUuid") String armaUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("relatoHechos") String relatoHechos
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaService.cambiarStatusCustodia(empresaUuid, licenciaUuid, armaUuid, username, relatoHechos, archivo);
    }
}
