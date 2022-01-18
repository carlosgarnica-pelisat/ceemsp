package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaFormaEjecucionDto;

import java.util.List;

public interface EmpresaFormaEjecucionService {
    List<EmpresaFormaEjecucionDto> obtenerFormasEjecucionPorEmpresaUuid(String empresaUuid);
    EmpresaFormaEjecucionDto crearFormaEjecucion(String empresaUuid, String username, EmpresaFormaEjecucionDto empresaFormaEjecucionDto);
}
