package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface EmpresaReporteMensualService {
    List<EmpresaReporteMensualDto> listarReportes(String username);
    EmpresaReporteMensualDto descargarReporteUuid(String uuid);
    EmpresaReporteMensualDto pregenerarReporte(String username);
    EmpresaReporteMensualDto guardarReporte(String username, EmpresaReporteMensualDto reporte) throws NoSuchAlgorithmException;
    List<PersonaDto> obtenerActivosPersonalPorReporte(String username, String reporteUuid);
    List<PersonaDto> obtenerAltasPersonalPorReporte(String username, String reporteUuid);
    List<PersonaDto> obtenerBajasPersonalPorReporte(String username, String reporteUuid);
    List<ClienteDto> obtenerActivosClientesPorReporte(String username, String reporteUuid);
    List<ClienteDto> obtenerAltasClientePorReporte(String username, String reporteUuid);
    List<ClienteDto> obtenerBajasClientePorReporte(String username, String reporteUuid);
    List<VehiculoDto> obtenerActivosVehiculosPorReporte(String username, String reporteUuid);
    List<VehiculoDto> obtenerAltasVehiculoPorReporte(String username, String reporteUuid);
    List<VehiculoDto> obtenerBajasVehiculoPorReporte(String username, String reporteUuid);
    List<ArmaDto> obtenerActivosArmasPorReporte(String username, String reporteUuid, String modalidad);
    List<ArmaDto> obtenerAltasArmasPorReporte(String username, String reporteUuid, String modalidad);
    List<ArmaDto> obtenerBajasArmasPorReporte(String username, String reporteUuid, String modalidad);
    List<CanDto> obtenerActivosCanesPorReporte(String username, String reporteUuid);
    List<CanDto> obtenerAltasCanesPorReporte(String username, String reporteUuid);
    List<CanDto> obtenerBajasCanesPorReporte(String username, String reporteUuid);

}
