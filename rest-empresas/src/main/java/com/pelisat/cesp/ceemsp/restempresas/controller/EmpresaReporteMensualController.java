package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaReporteMensualDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaReporteMensualService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaReporteMensualController {
    private final EmpresaReporteMensualService empresaReporteMensualService;
    private final JwtUtils jwtUtils;
    private static final String PERSONAL_REPORTES_MENSUALES_URI = "/reportes-mensuales";

    @Autowired
    public EmpresaReporteMensualController(EmpresaReporteMensualService empresaReporteMensualService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaReporteMensualService = empresaReporteMensualService;
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaReporteMensualDto> obtenerReportesPorEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.listarReportes(username);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaReporteMensualDto obtenerReportePorUuid(
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaReporteMensualService.descargarReporteUuid(reporteUuid);
    }

    @PostMapping(value = PERSONAL_REPORTES_MENSUALES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaReporteMensualDto crearReporte(
            HttpServletRequest request,
            @RequestBody EmpresaReporteMensualDto reporte
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.guardarReporte(username, reporte);
    }

    @PostMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/generar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaReporteMensualDto precargarReporte(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.pregenerarReporte(username);
    }
}
