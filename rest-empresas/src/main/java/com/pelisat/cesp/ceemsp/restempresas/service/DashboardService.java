package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;
import com.pelisat.cesp.ceemsp.database.dto.DashboardEmpresaDto;

public interface DashboardService {
    DashboardEmpresaDto obtenerDatosDashboard(String username);
}
