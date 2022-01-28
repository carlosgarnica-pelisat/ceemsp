package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaUniformeElementoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaUniformaElementoController {
    private final EmpresaUniformeElementoService empresaUniformeElementoService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_UNIFORME_ELEMENTOS_URI = "/empresas/{empresaUuid}/uniformes/{uniformeUuid}/elementos";

    @Autowired
    public EmpresaUniformaElementoController(EmpresaUniformeElementoService empresaUniformeElementoService, JwtUtils jwtUtils) {
        this.empresaUniformeElementoService = empresaUniformeElementoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_UNIFORME_ELEMENTOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaUniformeElementoDto> obtenerEmpresaUniformes(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return empresaUniformeElementoService.obtenerElementosUniformePorEmpresaUuid(empresaUuid, uniformeUuid);
    }

    @PostMapping(value = EMPRESA_UNIFORME_ELEMENTOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeElementoDto guardarUniforme(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @RequestBody EmpresaUniformeElementoDto uniformeDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeElementoService.guardarUniformeElemento(empresaUuid, uniformeUuid, username, uniformeDto);
    }
}
