package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaInformeMensualService;
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

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/xls")
    public ResponseEntity<InputStreamResource> generarInformeMensualExcel(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        File resultado = empresaInformeMensualService.generarInformeMensualExcel(empresaUuid, reporteUuid, username);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
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

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosActivosPersonal(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaInformeMensualService.obtenerActivosPersonalPorReporte(empresaUuid, reporteUuid, username);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosAltasPersonal(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaInformeMensualService.obtenerAltasPersonalPorReporte(empresaUuid, reporteUuid, username);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/personal/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerMovimientosBajasPersonal(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaInformeMensualService.obtenerBajasPersonalPorReporte(empresaUuid, reporteUuid, username);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/clientes/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerMovimientosActivosClientes(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerActivosClientesPorReporte(empresaUuid, reporteUuid);
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

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/vehiculos/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoDto> obtenerMovimientosVehiculosActivos(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerActivosVehiculosPorReporte(empresaUuid, reporteUuid);
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

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/armas/activas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerMovimientosArmasActivas(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid,
            @RequestParam(value = "modalidad") String modalidad
    ) {
        return empresaInformeMensualService.obtenerActivosArmasPorReporte(empresaUuid, reporteUuid, modalidad);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/armas/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerMovimientosAltasArmas(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid,
            @RequestParam(value = "modalidad") String modalidad
    ) {
        return empresaInformeMensualService.obtenerAltasArmasPorReporte(empresaUuid, reporteUuid, modalidad);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/armas/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerMovimientosBajasArmas(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid,
            @RequestParam(value = "modalidad") String modalidad
    ) {
        return empresaInformeMensualService.obtenerBajasArmasPorReporte(empresaUuid, reporteUuid, modalidad);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/canes/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanDto> obtenerMovimientosCanesActivos(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerActivosCanesPorReporte(empresaUuid, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/canes/altas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanDto> obtenerMovimientosAltasCanes(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerAltasCanesPorReporte(empresaUuid, reporteUuid);
    }

    @GetMapping(value = PERSONAL_REPORTES_MENSUALES_URI + "/{reporteUuid}/canes/bajas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanDto> obtenerMovimientosBajasCanes(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "reporteUuid") String reporteUuid
    ) {
        return empresaInformeMensualService.obtenerBajasCanesPorReporte(empresaUuid, reporteUuid);
    }
}
