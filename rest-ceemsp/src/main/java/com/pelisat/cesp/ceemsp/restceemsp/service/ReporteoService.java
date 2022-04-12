package com.pelisat.cesp.ceemsp.restceemsp.service;

import java.io.File;

public interface ReporteoService {
    File generarReporteListadoNominal() throws Exception;
    File generarReportePadronEmpresas() throws Exception;
}
