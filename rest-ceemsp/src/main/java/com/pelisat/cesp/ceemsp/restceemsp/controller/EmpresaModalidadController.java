package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaModalidadService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaModalidadController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_MODALIDAD_URI = "/empresas/{empresaUuid}/modalidades";
    private final EmpresaModalidadService empresaModalidadService;

    @Autowired
    public EmpresaModalidadController(JwtUtils jwtUtils, EmpresaModalidadService empresaModalidadService) {
        this.jwtUtils = jwtUtils;
        this.empresaModalidadService = empresaModalidadService;
    }

    @GetMapping(value = EMPRESA_MODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaModalidadDto> obtenerModalidadesPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaModalidadService.obtenerModalidadesEmpresa(empresaUuid);
    }

    @PostMapping(value = EMPRESA_MODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaModalidadDto guardarModalidad(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody EmpresaModalidadDto empresaModalidadDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaModalidadService.guardarModalidad(empresaUuid, username, empresaModalidadDto);
    }

    @DeleteMapping(value = EMPRESA_MODALIDAD_URI + "/{empresaModalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaModalidadDto eliminarModalidad(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "empresaModalidadUuid") String modalidadUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaModalidadService.eliminarModalidadPorUuid(empresaUuid, modalidadUuid, username);
    }
}
