package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalNacionalidadDto;

import java.util.List;

public interface PersonaService {
    List<PersonaDto> obtenerTodos(String empresaUuid);
    PersonaDto obtenerPorUuid(String empresaUuid, String personaUuid);
    PersonaDto obtenerPorId(String empresaUuid, Integer id);
    PersonaDto crearNuevo(PersonaDto personalDto, String username, String empresaUuid);
    PersonaDto modificarInformacionPuesto(PersonaDto personaDto, String username, String empresaUuid, String personaUuid);
    PersonaDto modificarPersona(String empresaUuid, String personaUuid, String username, PersonaDto personaDto);
    PersonaDto eliminarPersona(String empresaUuid, String personaUuid, String username);
}
