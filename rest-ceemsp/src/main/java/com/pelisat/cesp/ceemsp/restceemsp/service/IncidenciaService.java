package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;

import java.util.List;

public interface IncidenciaService {
    List<IncidenciaDto> obtenerIncidenciasPorEmpresa(String uuidEmpresa);

    IncidenciaDto obtenerIncidenciaPorUuid(String empresaUuid, String incidenciaUuid);

    IncidenciaDto guardarIncidencia(String empresaUuid, String username, IncidenciaDto incidenciaDto);
}
