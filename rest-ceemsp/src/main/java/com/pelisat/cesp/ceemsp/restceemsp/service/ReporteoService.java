package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReporteoService {
    File generarReporteListadoNominal(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    File generarReportePadronEmpresas(LocalDate fechaInicio, LocalDate fechafin, EmpresaStatusEnum empresaStatusEnum) throws Exception;
    File generarReporteIntercambioInformacion(LocalDate fechaInicio, LocalDate fechafin) throws Exception;
    File generarReporteAcuerdos(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReportePersonal(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReporteEscrituras(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReporteCanes(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReporteVehiculos(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReporteClientes(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReporteArmas(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReporteLicenciasColectivas(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
    File generarReporteVisitas(LocalDateTime fechaInicio, LocalDateTime fechafin) throws Exception;
}
