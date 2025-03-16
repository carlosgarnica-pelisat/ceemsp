package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;

public interface EmpresaIncidenciaPersonalService {
    PersonaDto agregarPersonaIncidencia(String incidenciaUuid, String username, PersonaDto personaDto);
    PersonaDto eliminarPersonaIncidencia(String incidenciaUuid, String personaIncidenciaUuid, String username);
}
