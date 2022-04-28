package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;

public interface IncidenciaArmaService {
    ArmaDto agregarArmaIncidencia(String empresaUuid, String incidenciaUuid, String username, ArmaDto armaDto);
    ArmaDto eliminarArmaIncidencia(String empresaUuid, String incidenciaUuid, String armaIncidenciaUuid, String username);
}
