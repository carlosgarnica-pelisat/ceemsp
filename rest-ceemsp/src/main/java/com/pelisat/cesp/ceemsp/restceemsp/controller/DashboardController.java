package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ConteoMensualDto;
import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaReporteMensualDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.DashboardService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
    private final JwtUtils jwtUtils;
    private static final String DASHBOARD_URI = "/dashboard";
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(JwtUtils jwtUtils, DashboardService dashboardService) {
        this.jwtUtils = jwtUtils;
        this.dashboardService = dashboardService;
    }

    @GetMapping(value = DASHBOARD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardDto getDashboardData(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return dashboardService.obtenerDatosDashboard(username);
    }

    @GetMapping(value = DASHBOARD_URI + "/movimientos-mes/{fechaInicio}/{fechaFin}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConteoMensualDto getMovimientosMes(
            @PathVariable(name = "fechaInicio") String fechaInicio,
            @PathVariable(name = "fechaFin") String fechaFin
    ) {
        return dashboardService.obtenerMovimientosMes(fechaInicio, fechaFin);
    }

    @GetMapping(value = DASHBOARD_URI + "/movimientos-mes/{fechaInicio}/{fechaFin}/empresas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaReporteMensualDto> getMovimientosMesEmpresas(
            @PathVariable(name = "fechaInicio") String fechaInicio,
            @PathVariable(name = "fechaFin") String fechaFin
    ) {
        return dashboardService.obtenerMovimientosMesEmpresas(fechaInicio, fechaFin);
    }

    @GetMapping(value = DASHBOARD_URI + "/con-movimientos/{fechaInicio}/{fechaFin}/empresas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaDto> obtenerEmpresasConMovimientosInformesMensuales(
            @PathVariable(name = "fechaInicio") String fechaInicio,
            @PathVariable(name = "fechaFin") String fechaFin
    ) {
        return dashboardService.obtenerEmpresasConMovimientosInformesMensuales(fechaInicio, fechaFin);
    }

    @GetMapping(value = DASHBOARD_URI + "/sin-movimientos/{fechaInicio}/{fechaFin}/empresas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaDto> obtenerEmpresasSinMovimientosInformesMensuales(
            @PathVariable(name = "fechaInicio") String fechaInicio,
            @PathVariable(name = "fechaFin") String fechaFin
    ) {
        return dashboardService.obtenerEmpresasSinMovimientosInformesMensuales(fechaInicio, fechaFin);
    }
}
