package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;

import java.util.List;

public interface IncidenciaArmaService {
    List<ArmaDto> obtenerArmasIncidencia(String empresaUuid, String incidenciaUuid);
    List<ArmaDto> obtenerArmasEliminadasIncidencia(String empresaUuid, String incidenciaUuid);
    ArmaDto agregarArmaIncidencia(String empresaUuid, String incidenciaUuid, String username, ArmaDto armaDto);
    ArmaDto eliminarArmaIncidencia(String empresaUuid, String incidenciaUuid, String armaIncidenciaUuid, String username, ArmaDto armaDto);
}
