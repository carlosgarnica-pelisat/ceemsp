package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.DashboardEmpresaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.DashboardService;
import com.pelisat.cesp.ceemsp.restempresas.service.DashboardServiceImpl;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
    private static final String DASHBOARD_URI = "/dashboard";
    private final JwtUtils jwtUtils;
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService, JwtUtils jwtUtils) {
        this.dashboardService = dashboardService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = DASHBOARD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardEmpresaDto getDashboardData(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return dashboardService.obtenerDatosDashboard(username);
    }
}
