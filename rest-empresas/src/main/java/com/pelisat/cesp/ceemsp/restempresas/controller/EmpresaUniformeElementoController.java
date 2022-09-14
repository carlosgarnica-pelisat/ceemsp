package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaUniformeElementoService;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaUniformeService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaUniformeElementoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_UNIFORME_ELEMENTO_URI = "/uniformes/{uniformeUuid}/{elementos}";
    private final EmpresaUniformeElementoService empresaUniformeElementoService;

    @Autowired
    public EmpresaUniformeElementoController(EmpresaUniformeElementoService empresaUniformeElementoService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaUniformeElementoService = empresaUniformeElementoService;
    }

    @GetMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaUniformeElementoDto> obtenerUniformesPorEmpresa(
            HttpServletRequest request,
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return empresaUniformeElementoService.obtenerElementosUniformePorEmpresaUuid(uniformeUuid);
    }

    @PostMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeElementoDto crearUniformeElemento(
            HttpServletRequest request,
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @RequestBody EmpresaUniformeElementoDto empresaUniformeElementoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeElementoService.guardarUniformeElemento(uniformeUuid, username, empresaUniformeElementoDto);
    }

    @PutMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI + "/{elementoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeElementoDto modificarUniformeElemento(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @PathVariable(value = "elementoUuid") String elementoUuid,
            HttpServletRequest request,
            @RequestBody EmpresaUniformeElementoDto empresaUniformeElementoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeElementoService.modificarUniformeElemento(uniformeUuid, elementoUuid, username, empresaUniformeElementoDto);
    }

    @DeleteMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI + "/{elementoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeElementoDto eliminarUniforme(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @PathVariable(value = "elementoUuid") String elementoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeElementoService.eliminarUniformeElemento(uniformeUuid, elementoUuid, username);
    }
}
