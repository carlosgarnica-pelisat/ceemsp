package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEscrituraApoderadoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaLegalApoderadosController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_APODERADOS_URI = "/escrituras/{escrituraUuid}/apoderados";
    private final EmpresaEscrituraApoderadoService empresaEscrituraApoderadoService;

    @Autowired
    public EmpresaLegalApoderadosController(EmpresaEscrituraApoderadoService empresaEscrituraApoderadoService,
                                               JwtUtils jwtUtils) {
        this.empresaEscrituraApoderadoService = empresaEscrituraApoderadoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_APODERADOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraApoderadoService.obtenerApoderadosPorEscritura(escrituraUuid);
    }

    @PostMapping(value = EMPRESA_APODERADOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto crearApoderado(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.crearApoderado(escrituraUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_APODERADOS_URI + "/{apoderadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto modificarApoderado(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String apoderadoUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.modificarApoderado(escrituraUuid, apoderadoUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_APODERADOS_URI + "/{apoderadoUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaEscrituraApoderadoDto eliminarApoderado(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String apoderadoUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("apoderado") String apoderado,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.eliminarApoderado(escrituraUuid, apoderadoUuid, username, new Gson().fromJson(apoderado, EmpresaEscrituraApoderadoDto.class), archivo);
    }
}
