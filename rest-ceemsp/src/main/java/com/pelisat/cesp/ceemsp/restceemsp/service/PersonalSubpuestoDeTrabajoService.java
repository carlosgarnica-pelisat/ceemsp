package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalPuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalSubpuestoDeTrabajoDto;

import java.util.List;

public interface PersonalSubpuestoDeTrabajoService {
    List<PersonalSubpuestoDeTrabajoDto> obtenerTodos(String uuid);

    PersonalSubpuestoDeTrabajoDto obtenerPorUuid(String uuid, String subpuestoUuid);

    PersonalSubpuestoDeTrabajoDto obtenerPorId(Integer id);

    PersonalSubpuestoDeTrabajoDto crearNuevo(PersonalSubpuestoDeTrabajoDto personalSubpuestoDeTrabajoDto, String username, String uuid);
}
