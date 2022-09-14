package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraSocioService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaEscrituraSocioController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_SOCIOS_URI = "/empresas/{empresaUuid}/escrituras/{escrituraUuid}/socios";
    private final EmpresaEscrituraSocioService empresaEscrituraSocioService;

    @Autowired
    public EmpresaEscrituraSocioController(EmpresaEscrituraSocioService empresaEscrituraSocioService,
                                           JwtUtils jwtUtils) {
        this.empresaEscrituraSocioService = empresaEscrituraSocioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_SOCIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraSocioDto> obtenerSociosPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraSocioService.obtenerSociosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_SOCIOS_URI + "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraSocioDto> obtenerTodosSociosPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraSocioService.obtenerTodosSociosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_SOCIOS_URI + "/{socioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraSocioDto obtenerEscrituraSocioPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "socioUuid") String socioUuid
    ) {
        return empresaEscrituraSocioService.obtenerSocioPorUuid(empresaUuid, escrituraUuid, socioUuid, false);
    }

    @PostMapping(value = EMPRESA_SOCIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraSocioDto crearEscrituraSocio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraSocioDto empresaEscrituraSocioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraSocioService.crearSocio(empresaUuid, escrituraUuid, username, empresaEscrituraSocioDto);
    }

    @PutMapping(value = EMPRESA_SOCIOS_URI + "/{socioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraSocioDto modificarEscrituraSocio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "socioUuid") String socioUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraSocioDto empresaEscrituraSocioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraSocioService.modificarSocio(empresaUuid, escrituraUuid, socioUuid, username, empresaEscrituraSocioDto);
    }

    @PutMapping(value = EMPRESA_SOCIOS_URI + "/{socioUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaEscrituraSocioDto eliminarEscrituraSocio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "socioUuid") String socioUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("socio") String socio,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraSocioService.eliminarSocio(empresaUuid, escrituraUuid, socioUuid, username, new Gson().fromJson(socio, EmpresaEscrituraSocioDto.class), archivo);
    }
}
