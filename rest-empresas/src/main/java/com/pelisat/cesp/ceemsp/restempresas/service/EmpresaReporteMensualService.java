package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaReporteMensualDto;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface EmpresaReporteMensualService {
    List<EmpresaReporteMensualDto> listarReportes(String username);
    EmpresaReporteMensualDto descargarReporteUuid(String uuid);
    EmpresaReporteMensualDto pregenerarReporte(String username);
    EmpresaReporteMensualDto guardarReporte(String username, EmpresaReporteMensualDto reporte) throws NoSuchAlgorithmException;

}
