package com.pelisat.cesp.ceemsp.restceemsp.service;

import java.io.File;

public interface ReporteEmpresaService {
    File generarReporteDomicilios(String empresaUuid) throws Exception;
    File generarReporteAcuerdos(String empresaUuid) throws Exception;
    File generarReportePersonal(String empresaUuid, boolean eliminados, String username) throws Exception;
    File generarReporteEscrituras(String empresaUuid) throws Exception;
    File generarReporteCanes(String empresaUuid, boolean eliminados) throws Exception;
    File generarReporteVehiculos(String empresaUuid, boolean eliminados) throws Exception;
    File generarReporteClientes(String empresaUuid) throws Exception;
    File generarReporteArmas(String empresaUuid, boolean eliminados) throws Exception;
    File generarReporteLicenciasColectivas(String empresaUuid) throws Exception;
    File generarReporteVisitas(String empresaUuid) throws Exception;
    File generarReporteEquipo(String empresaUuid) throws Exception;
}
