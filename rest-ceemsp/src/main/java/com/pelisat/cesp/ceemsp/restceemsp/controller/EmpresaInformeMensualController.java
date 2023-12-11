package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaReporteMensualDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaInformeMensualService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaInformeMensualController {
    private final EmpresaInformeMensualService empresaInformeMensualService;
    private final JwtUtils jwtUtils;
    private static final String PERSONAL_REPORTES_MENSUALES_URI = "/empresas/{empresaUuid}/informes-mensuales";

    @Autowired
    public EmpresaInformeMensualController(EmpresaInformeMensualService empresaInformeMensualService, JwtUtils jwtUtils) {
        this.empresaInformeMensualService = empresaInformeMensualService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaReporteMensualDto> obtenerReportesPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaInformeMensualService.listarReportes(empresaUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaReporteMensualDto obtenerReportePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.descargarReporteUuid(empresaUuid, reporteUuid);
    }

    @DeleteMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaReporteMensualDto eliminarReportePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaInformeMensualService.eliminarReporteUuid(empresaUuid, reporteUuid, username);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosAltasPersonal(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerAltasPersonalPorReporte(empresaUuid, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosBajasPersonal(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerBajasPersonalPorReporte(empresaUuid, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/clientes/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerMovimientosAltasClientes(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerAltasClientePorReporte(empresaUuid, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/clientes/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerMovimientosBajasClientes(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerBajasClientePorReporte(empresaUuid, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/vehiculos/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoDto> obtenerMovimientosAltasVehiculos(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerAltasVehiculoPorReporte(empresaUuid, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/vehiculos/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoDto> obtenerMovimientosBajasVehiculos(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerBajasVehiculoPorReporte(empresaUuid, reporteUuid);
    }
}
