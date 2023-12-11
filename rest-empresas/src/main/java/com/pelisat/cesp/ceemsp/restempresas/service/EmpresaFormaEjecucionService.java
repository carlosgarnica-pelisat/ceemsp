package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaFormaEjecucionDto;

import java.util.List;

public interface EmpresaFormaEjecucionService {
    List<EmpresaFormaEjecucionDto> obtenerFormasEjecucionEmpresa(String username);
}
