package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;

public interface DashboardService {
    DashboardDto obtenerDatosDashboard(String username);
}
