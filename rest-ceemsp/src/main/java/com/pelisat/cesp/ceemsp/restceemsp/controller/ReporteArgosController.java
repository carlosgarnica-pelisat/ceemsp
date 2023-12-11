package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ReporteArgosDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ReporteArgosService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReporteArgosController {
    private final ReporteArgosService reporteArgosService;
    private final JwtUtils jwtUtils;

    @Autowired
    public ReporteArgosController(ReporteArgosService reporteArgosService, JwtUtils jwtUtils) {
        this.reporteArgosService = reporteArgosService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = "/reportes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReporteArgosDto> descargarReportes() {
        return reporteArgosService.obtenerReportes();
    }

    @PostMapping(value = "/reportes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReporteArgosDto crearReporte(
            HttpServletRequest request,
            @RequestBody ReporteArgosDto reporteArgosDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return reporteArgosService.crearReporte(reporteArgosDto, username);
    }

    @GetMapping(value = "/reportes/{reporteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReporteArgosDto obtenerReporteUuid(
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return reporteArgosService.obtenerReportePorUuid(reporteUuid);
    }

    @GetMapping(value = "/reportes/{reporteUuid}/archivo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarArchivo(
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) throws Exception {
        File file = reporteArgosService.descargarReporte(reporteUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }
}
