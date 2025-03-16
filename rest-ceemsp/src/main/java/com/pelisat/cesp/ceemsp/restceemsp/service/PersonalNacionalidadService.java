package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalNacionalidadDto;

import java.util.List;

public interface PersonalNacionalidadService {
    List<PersonalNacionalidadDto> obtenerTodos();
    PersonalNacionalidadDto obtenerPorUuid(String uuid);
    PersonalNacionalidadDto obtenerPorId(Integer id);
    PersonalNacionalidadDto crearNuevo(PersonalNacionalidadDto personalNacionalidadDto, String username);
    PersonalNacionalidadDto modificarNacionalidad(String uuid, String username, PersonalNacionalidadDto personalNacionalidadDto);
    PersonalNacionalidadDto eliminarNacionalidad(String uuid, String username);
}
