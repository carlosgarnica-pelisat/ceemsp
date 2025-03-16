package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaModalidadService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaModalidadController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_MODALIDAD_URI = "/modalidades";
    private final EmpresaModalidadService empresaModalidadService;

    @Autowired
    public EmpresaModalidadController(JwtUtils jwtUtils, EmpresaModalidadService empresaModalidadService) {
        this.empresaModalidadService = empresaModalidadService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_MODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaModalidadDto> obtenerModalidadesPorEmpresa(
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaModalidadService.obtenerModalidadesEmpresa(username);
    }

    @PostMapping(value = EMPRESA_MODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaModalidadDto guardarModalidad(
            @RequestBody EmpresaModalidadDto empresaModalidadDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaModalidadService.guardarModalidad(username, empresaModalidadDto);
    }

    @DeleteMapping(value = EMPRESA_MODALIDAD_URI + "/{empresaModalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaModalidadDto eliminarModalidad(
            @PathVariable(value = "empresaModalidadUuid") String modalidadUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaModalidadService.eliminarModalidadPorUuid(modalidadUuid, username);
    }
}
