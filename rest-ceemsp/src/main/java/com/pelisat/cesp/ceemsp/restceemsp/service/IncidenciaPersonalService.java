package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;

public interface IncidenciaPersonalService {
    PersonaDto agregarPersonaIncidencia(String empresaUuid, String incidenciaUuid, String username, PersonaDto personaDto);
    PersonaDto eliminarPersonaIncidencia(String empresaUuid, String incidenciaUuid, String personaIncidenciaUuid, String username);
}
