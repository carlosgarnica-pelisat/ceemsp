package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaReporteMensualService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosActivosPersonal(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerActivosPersonalPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosAltasPersonal(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerAltasPersonalPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosBajasPersonal(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerBajasPersonalPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/clientes/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerMovimientosActivosClientes(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerActivosClientesPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/clientes/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerMovimientosAltasClientes(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerAltasClientePorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/clientes/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerMovimientosBajasClientes(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerBajasClientePorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/vehiculos/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoDto> obtenerMovimientosVehiculosActivos(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerActivosVehiculosPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/vehiculos/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoDto> obtenerMovimientosAltasVehiculos(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerAltasVehiculoPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/vehiculos/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoDto> obtenerMovimientosBajasVehiculos(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerBajasVehiculoPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/armas/activas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerMovimientosArmasActivas(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            @RequestParam(value = "modalidad") String modalidad,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerActivosArmasPorReporte(username, reporteUuid, modalidad);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/armas/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerMovimientosAltasArmas(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            @RequestParam(value = "modalidad") String modalidad,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerAltasArmasPorReporte(username, reporteUuid, modalidad);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/armas/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerMovimientosBajasArmas(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            @RequestParam(value = "modalidad") String modalidad,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerBajasArmasPorReporte(username, reporteUuid, modalidad);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/canes/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanDto> obtenerMovimientosCanesActivos(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerActivosCanesPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/canes/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanDto> obtenerMovimientosAltasCanes(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerAltasCanesPorReporte(username, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/canes/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanDto> obtenerMovimientosBajasCanes(
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaReporteMensualService.obtenerBajasCanesPorReporte(username, reporteUuid);
    }
}
