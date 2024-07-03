package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface EmpresaInformeMensualService {
    List<EmpresaReporteMensualDto> listarReportes(String uuid);
    EmpresaReporteMensualDto descargarReporteUuid(String uuid, String reporteUuid);
    EmpresaReporteMensualDto eliminarReporteUuid(String uuid, String reporteUuid, String username);
    File generarInformeMensualExcel(String uuid, String reporteUuid, String username) throws IOException;
    List<PersonaDto> obtenerActivosPersonalPorReporte(String uuid, String reporteUuid, String username);
    List<PersonaDto> obtenerAltasPersonalPorReporte(String uuid, String reporteUuid, String username);
    List<PersonaDto> obtenerBajasPersonalPorReporte(String uuid, String reporteUuid, String username);
    List<ClienteDto> obtenerActivosClientesPorReporte(String uuid, String reporteUuid);
    List<ClienteDto> obtenerAltasClientePorReporte(String uuid, String reporteUuid);
    List<ClienteDto> obtenerBajasClientePorReporte(String uuid, String reporteUuid);
    List<VehiculoDto> obtenerActivosVehiculosPorReporte(String uuid, String reporteUuid);
    List<VehiculoDto> obtenerAltasVehiculoPorReporte(String uuid, String reporteUuid);
    List<VehiculoDto> obtenerBajasVehiculoPorReporte(String uuid, String reporteUuid);
    List<ArmaDto> obtenerActivosArmasPorReporte(String uuid, String reporteUuid, String modalidad);
    List<ArmaDto> obtenerAltasArmasPorReporte(String uuid, String reporteUuid, String modalidad);
    List<ArmaDto> obtenerBajasArmasPorReporte(String uuid, String reporteUuid, String modalidad);
    List<CanDto> obtenerActivosCanesPorReporte(String uuid, String reporteUuid);
    List<CanDto> obtenerAltasCanesPorReporte(String uuid, String reporteUuid);
    List<CanDto> obtenerBajasCanesPorReporte(String uuid, String reporteUuid);
}
