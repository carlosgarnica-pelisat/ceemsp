package com.pelisat.cesp.ceemsp.restceemsp.service;

import java.io.File;

public interface ReporteEmpresaService {
    File generarReporteAcuerdos(String empresaUuid) throws Exception;
    File generarReportePersonal(String empresaUuid) throws Exception;
    File generarReporteEscrituras(String empresaUuid) throws Exception;
    File generarReporteCanes(String empresaUuid) throws Exception;
    File generarReporteVehiculos(String empresaUuid) throws Exception;
    File generarReporteClientes(String empresaUuid) throws Exception;
    File generarReporteArmas(String empresaUuid) throws Exception;
    File generarReporteLicenciasColectivas(String empresaUuid) throws Exception;
    File generarReporteVisitas(String empresaUuid) throws Exception;
}
