package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ConteoMensualDto;
import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaReporteMensualDto;

import java.util.List;

public interface DashboardService {
    DashboardDto obtenerDatosDashboard(String username);
    ConteoMensualDto obtenerMovimientosMes(String fechaInicio, String fechaFin);
    List<EmpresaReporteMensualDto> obtenerMovimientosMesEmpresas(String fechaInicio, String fechaFin);
    List<EmpresaDto> obtenerEmpresasConMovimientosInformesMensuales(String fechaInicio, String fechaFin);
    List<EmpresaDto> obtenerEmpresasSinMovimientosInformesMensuales(String fechaInicio, String fechaFin);
}
