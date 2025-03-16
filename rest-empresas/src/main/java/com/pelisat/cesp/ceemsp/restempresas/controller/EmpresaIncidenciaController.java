package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaIncidenciaService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaIncidenciaController {

    private final JwtUtils jwtUtils;
    private static final String INCIDENCIA_URI = "/incidencias";
    private final EmpresaIncidenciaService empresaIncidenciaService;

    @Autowired
    public EmpresaIncidenciaController(JwtUtils jwtUtils, EmpresaIncidenciaService empresaIncidenciaService) {
        this.empresaIncidenciaService = empresaIncidenciaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = INCIDENCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IncidenciaDto> obtenerIncidenciasPorEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaIncidenciaService.obtenerIncidenciasPorEmpresa(username);
    }

    @GetMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaDto obtenerIncidenciaPorUuid(
            HttpServletRequest request,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaIncidenciaService.obtenerIncidenciaPorUuid(username, incidenciaUuid);
    }

    @PostMapping(value = INCIDENCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IncidenciaDto guardarIncidencia(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("incidencia") String incidencia,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaService.guardarIncidencia(username, new Gson().fromJson(incidencia, IncidenciaDto.class), archivo);
    }

    @PostMapping(value = INCIDENCIA_URI + "/{incidenciaUuid}/comentarios", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IncidenciaDto agregarComentario(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "archivo", required = false) MultipartFile file,
            @RequestParam(value = "comentario") String comentario
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaService.agregarComentario(incidenciaUuid, username, new Gson().fromJson(comentario, IncidenciaDto.class), file);
    }
}
