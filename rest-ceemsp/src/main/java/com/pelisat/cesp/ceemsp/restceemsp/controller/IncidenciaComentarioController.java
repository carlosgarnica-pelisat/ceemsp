package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaComentarioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaComentarioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class IncidenciaComentarioController {
    private final JwtUtils jwtUtils;
    private final IncidenciaComentarioService incidenciaComentarioService;
    private static final String INCIDENCIA_COMENTARIO_URI = "/empresas/{empresaUuid}/incidencias/{incidenciaUuid}/comentarios";

    @Autowired
    public IncidenciaComentarioController(JwtUtils jwtUtils, IncidenciaComentarioService incidenciaComentarioService) {
        this.jwtUtils = jwtUtils;
        this.incidenciaComentarioService = incidenciaComentarioService;
    }

    @GetMapping(value = INCIDENCIA_COMENTARIO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IncidenciaComentarioDto> obtenerComentariosPorIncidenciaUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid
    ) {
        return incidenciaComentarioService.obtenerComentariosPorIncidenciaUuid(empresaUuid, incidenciaUuid);
    }

    @PutMapping(value = INCIDENCIA_COMENTARIO_URI + "/{comentarioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaComentarioDto modificarComentario(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "comentarioUuid") String comentarioUuid,
            HttpServletRequest httpServletRequest,
            @RequestBody IncidenciaComentarioDto incidenciaComentarioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaComentarioService.modificarComentario(empresaUuid, incidenciaUuid, comentarioUuid, username, incidenciaComentarioDto);
    }

    @DeleteMapping(value = INCIDENCIA_COMENTARIO_URI + "/{comentarioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaComentarioDto eliminarComentario(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "comentarioUuid") String comentarioUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaComentarioService.eliminarComentario(empresaUuid, incidenciaUuid, comentarioUuid, username);
    }
}
