package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ReporteArgosDto;

import java.io.File;
import java.util.List;

public interface ReporteArgosService {
    List<ReporteArgosDto> obtenerReportes();
    File descargarReporte(String reporteUuid);
    ReporteArgosDto obtenerReportePorUuid(String reporteUuid);
    ReporteArgosDto crearReporte(ReporteArgosDto reporteArgosDto, String username);
    ReporteArgosDto eliminarReporte(String reporteUuid, String username);
}
