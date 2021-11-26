package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalPuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoTipoDto;

import java.util.List;

public interface PersonalPuestoDeTrabajoService {
    List<PersonalPuestoDeTrabajoDto> obtenerTodos();

    PersonalPuestoDeTrabajoDto obtenerPorUuid(String uuid);

    PersonalPuestoDeTrabajoDto obtenerPorId(Integer id);

    PersonalPuestoDeTrabajoDto crearNuevo(PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto, String username);
}
