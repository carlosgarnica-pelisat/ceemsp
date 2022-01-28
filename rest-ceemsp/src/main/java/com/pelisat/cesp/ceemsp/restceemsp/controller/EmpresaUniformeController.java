package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaUniformeService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaUniformeController {
    private final EmpresaUniformeService empresaUniformeService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_UNIFORMES_URI = "/empresas/{empresaUuid}/uniformes";

    @Autowired
    public EmpresaUniformeController(EmpresaUniformeService empresaUniformeService, JwtUtils jwtUtils) {
        this.empresaUniformeService = empresaUniformeService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_UNIFORMES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaUniformeDto> obtenerEmpresaUniformes(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaUniformeService.obtenerUniformesPorEmpresaUuid(empresaUuid);
    }

    @GetMapping(value = EMPRESA_UNIFORMES_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto obtenerEmpresaUniformePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return empresaUniformeService.obtenerUniformePorUuid(empresaUuid, uniformeUuid);
    }

    @PostMapping(value = EMPRESA_UNIFORMES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto guardarUniforme(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody EmpresaUniformeDto uniformeDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.guardarUniforme(empresaUuid, username, uniformeDto);
    }
}
