package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEquipoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaUniformeService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaEquipoController {
    private final EmpresaEquipoService empresaEquipoService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_EQUIPOS_URI = "/empresas/{empresaUuid}/equipos";

    @Autowired
    public EmpresaEquipoController(EmpresaEquipoService empresaUniformeService, JwtUtils jwtUtils) {
        this.empresaEquipoService = empresaUniformeService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_EQUIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEquipoDto> obtenerEmpresaEquipos(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaEquipoService.obtenerEquiposPorEmpresaUuid(empresaUuid);
    }

    @GetMapping(value = EMPRESA_EQUIPOS_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEquipoDto obtenerEmpresaUniformePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "equipoUuid") String equipoUuid
    ) {
        return empresaEquipoService.obtenerEquipoPorUuid(empresaUuid, equipoUuid);
    }

    @PostMapping(value = EMPRESA_EQUIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEquipoDto guardarEquipo(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody EmpresaEquipoDto equipoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEquipoService.guardarEquipo(empresaUuid, username, equipoDto);
    }
}
