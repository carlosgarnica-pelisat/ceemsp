package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEquipoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEquipoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
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
    private static final String EMPRESA_EQUIPOS_URI = "/equipos";

    @Autowired
    public EmpresaEquipoController(EmpresaEquipoService empresaUniformeService, JwtUtils jwtUtils) {
        this.empresaEquipoService = empresaUniformeService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_EQUIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEquipoDto> obtenerEmpresaEquipos(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEquipoService.obtenerEquiposPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_EQUIPOS_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEquipoDto obtenerEmpresaUniformePorUuid(
            @PathVariable(value = "equipoUuid") String equipoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEquipoService.obtenerEquipoPorUuid(equipoUuid, username);
    }

    @PostMapping(value = EMPRESA_EQUIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEquipoDto guardarEquipo(
            HttpServletRequest request,
            @RequestBody EmpresaEquipoDto equipoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEquipoService.guardarEquipo(equipoDto, username);
    }

    @PutMapping(value = EMPRESA_EQUIPOS_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEquipoDto modificarEquipo(
            HttpServletRequest request,
            @PathVariable(value = "equipoUuid") String equipoUuid,
            @RequestBody EmpresaEquipoDto equipoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEquipoService.modificarEquipo(equipoUuid, username, equipoDto);
    }

    @DeleteMapping(value = EMPRESA_EQUIPOS_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEquipoDto eliminarEquipo(
            HttpServletRequest request,
            @PathVariable(value = "equipoUuid") String equipoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEquipoService.eliminarEquipo(equipoUuid, username);
    }
}
