package com.pelisat.cesp.ceemsp.restceemsp.service;

import java.io.File;
import java.time.LocalDate;

public interface ReporteoService {
    File generarReporteListadoNominal(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    File generarReportePadronEmpresas(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteIntercambioInformacion(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteAcuerdos(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReportePersonal(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteEscrituras(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteCanes(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteVehiculos(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteClientes(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteArmas(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteLicenciasColectivas(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteVisitas(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
}
