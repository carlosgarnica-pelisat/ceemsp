package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEscrituraService;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaUniformeService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaUniformeController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_UNIFORME_URI = "/uniformes";
    private final EmpresaUniformeService empresaUniformeService;

    @Autowired
    public EmpresaUniformeController(EmpresaUniformeService empresaUniformeService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaUniformeService = empresaUniformeService;
    }

    @GetMapping(value = EMPRESA_UNIFORME_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaUniformeDto> obtenerUniformesPorEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.obtenerUniformesPorEmpresaUuid(username);
    }

    @GetMapping(value = EMPRESA_UNIFORME_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto obtenerEscrituraPorUuid(
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return empresaUniformeService.obtenerUniformePorUuid(uniformeUuid);
    }

    @PostMapping(value = EMPRESA_UNIFORME_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto crearUniforme(
            HttpServletRequest request,
            @RequestBody EmpresaUniformeDto empresaUniformeDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.guardarUniforme(username, empresaUniformeDto);
    }

    @PutMapping(value = EMPRESA_UNIFORME_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto modificarUniforme(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            HttpServletRequest request,
            @RequestBody EmpresaUniformeDto empresaUniformeDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.modificarUniforme(uniformeUuid, username, empresaUniformeDto);
    }

    @DeleteMapping(value = EMPRESA_UNIFORME_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto eliminarUniforme(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.eliminarUniforme(uniformeUuid, username);
    }
}
