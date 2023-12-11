package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.DashboardService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
}
