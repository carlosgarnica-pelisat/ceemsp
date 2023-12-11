package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class IncidenciaController {
    private final JwtUtils jwtUtils;
    private static final String INCIDENCIA_URI = "/empresas/{empresaUuid}/incidencias";
    private final IncidenciaService incidenciaService;

    @Autowired
    public IncidenciaController(
            JwtUtils jwtUtils,
            IncidenciaService incidenciaService
    ) {
        this.jwtUtils = jwtUtils;
        this.incidenciaService = incidenciaService;
    }

    @GetMapping(value = INCIDENCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IncidenciaDto> obtenerIncidenciasPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return incidenciaService.obtenerIncidenciasPorEmpresa(empresaUuid);
    }

    @GetMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto obtenerIncidenciaPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid
    ) {
        return incidenciaService.obtenerIncidenciaPorUuid(empresaUuid, incidenciaUuid);
    }

    @PostMapping(value = INCIDENCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IncidenciaDto guardarIncidencia(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("incidencia") String incidencia,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaService.guardarIncidencia(empresaUuid, username, new Gson().fromJson(incidencia, IncidenciaDto.class), archivo);
    }

    @PostMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}/comentarios", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto agregarComentario(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            HttpServletRequest httpServletRequest,
            @RequestBody IncidenciaDto incidenciaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaService.agregarComentario(empresaUuid, incidenciaUuid, username, incidenciaDto);
    }

    @PutMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}/autoasignar", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto autoasignarIncidencia(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaService.autoasignarIncidencia(empresaUuid, incidenciaUuid, username);
    }

    @PutMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}/asignar", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto asignarIncidencia(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            HttpServletRequest httpServletRequest,
            @RequestBody UsuarioDto usuarioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaService.asignarIncidencia(empresaUuid, incidenciaUuid, usuarioDto, username);
    }

    @DeleteMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto eliminarIncidencia(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaService.eliminarIncidencia(empresaUuid, incidenciaUuid, username);
    }
}
