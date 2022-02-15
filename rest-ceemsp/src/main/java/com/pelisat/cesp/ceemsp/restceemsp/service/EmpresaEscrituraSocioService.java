package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;

import java.util.List;

public interface EmpresaEscrituraSocioService {
    List<EmpresaEscrituraSocioDto> obtenerSociosPorEscritura(String empresaUuid, String escrituraUuid);
    EmpresaEscrituraSocioDto obtenerSocioPorUuid(String empresaUuid, String escrituraUuid, String socioUuid, boolean soloEntidad);
    EmpresaEscrituraSocioDto crearSocio(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto);
    EmpresaEscrituraSocioDto modificarSocio(String empresaUuid, String escrituraUuid, String socioUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto);
    EmpresaEscrituraSocioDto eliminarSocio(String empresaUuid, String escrituraUuid, String socioUuid, String username);
}
