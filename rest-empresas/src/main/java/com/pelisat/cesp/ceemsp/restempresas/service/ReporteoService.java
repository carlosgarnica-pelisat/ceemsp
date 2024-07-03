package com.pelisat.cesp.ceemsp.restempresas.service;

import java.io.File;

public interface ReporteoService {
    File generarReporteAcuerdos(String username) throws Exception;
    File generarReportePersonal(String username, boolean eliminados) throws Exception;
    File generarReporteEscrituras(String username) throws Exception;
    File generarReporteCanes(String username, boolean eliminados) throws Exception;
    File generarReporteVehiculos(String username, boolean eliminados) throws Exception;
    File generarReporteClientes(String username, boolean eliminados) throws Exception;
    File generarReporteArmas(String username) throws Exception;
    File generarReporteLicenciasColectivas(String username) throws Exception;
    File generarReporteVisitas(String username) throws Exception;
}
