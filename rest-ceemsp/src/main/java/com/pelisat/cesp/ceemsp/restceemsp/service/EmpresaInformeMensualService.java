package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaReporteMensualDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;

import java.util.List;

public interface EmpresaInformeMensualService {
    List<EmpresaReporteMensualDto> listarReportes(String uuid);
    EmpresaReporteMensualDto descargarReporteUuid(String uuid, String reporteUuid);
    EmpresaReporteMensualDto eliminarReporteUuid(String uuid, String reporteUuid, String username);
    List<PersonaDto> obtenerAltasPersonalPorReporte(String uuid, String reporteUuid);
    List<PersonaDto> obtenerBajasPersonalPorReporte(String uuid, String reporteUuid);
    List<ClienteDto> obtenerAltasClientePorReporte(String uuid, String reporteUuid);
    List<ClienteDto> obtenerBajasClientePorReporte(String uuid, String reporteUuid);
    List<VehiculoDto> obtenerAltasVehiculoPorReporte(String uuid, String reporteUuid);
    List<VehiculoDto> obtenerBajasVehiculoPorReporte(String uuid, String reporteUuid);
}
