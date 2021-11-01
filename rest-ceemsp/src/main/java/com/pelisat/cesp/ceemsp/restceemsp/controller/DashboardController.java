package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
    private final JwtUtils jwtUtils;
    private static final String DASHBOARD_URI = "/dashboard";

    @Autowired
    public DashboardController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // TODO: Confirmar con los miembros del CEEMSP la informacion del dashboard
    @PostMapping(value = DASHBOARD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardDto getDashboardData() {
        return new DashboardDto();
    }
}
