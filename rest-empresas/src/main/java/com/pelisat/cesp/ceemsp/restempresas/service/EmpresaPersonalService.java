package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;

import java.util.List;

public interface EmpresaPersonalService {
    List<PersonaDto> obtenerTodos(String username);
    PersonaDto obtenerPorUuid(String personaUuid);
    PersonaDto obtenerPorId(Integer id);
    PersonaDto crearNuevo(PersonaDto personalDto, String username);
    PersonaDto modificarInformacionPuesto(PersonaDto personaDto, String username, String personaUuid);
    PersonaDto modificarPersona(String personaUuid, String username, PersonaDto personaDto);
    PersonaDto eliminarPersona(String personaUuid, String username);


}
