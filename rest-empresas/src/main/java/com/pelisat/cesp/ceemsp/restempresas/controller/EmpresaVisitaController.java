package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaVisitaService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaVisitaController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_VISITAS_URI = "/visitas";
    private final EmpresaVisitaService empresaVisitaService;

    @Autowired
    public EmpresaVisitaController(JwtUtils jwtUtils, EmpresaVisitaService empresaVisitaService) {
        this.jwtUtils = jwtUtils;
        this.empresaVisitaService = empresaVisitaService;
    }

    @GetMapping(value = EMPRESA_VISITAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VisitaDto> obtenerVisitas(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaVisitaService.obtenerVisitasPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_VISITAS_URI + "/{visitaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VisitaDto obtenerVisitaPorUuid(
            @PathVariable(value = "visitaUuid") String visitaUuid
    ) {
        return empresaVisitaService.obtenerVisitaPorUuid(visitaUuid);
    }

    @GetMapping(value = EMPRESA_VISITAS_URI + "/{visitaUuid}/archivos/{archivoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> obtenerArchivoVisitaPorUuid(
            @PathVariable(value = "visitaUuid") String visitaUuid,
            @PathVariable(value = "archivoUuid") String archivoUuid
    ) throws Exception {
        File file = empresaVisitaService.descargarArchivoVisita(visitaUuid, archivoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }
}
