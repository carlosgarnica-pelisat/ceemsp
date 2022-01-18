package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EquipoDto;

import java.util.List;

public interface EquipoService {
    List<EquipoDto> obtenerEquipos();
    EquipoDto obtenerEquipoPorUuid(String equipoUuid);
    EquipoDto guardarEquipo(EquipoDto equipoDto, String username);
    EquipoDto modificarEquipo(String equipoUuid, String username, EquipoDto equipoDto);
    EquipoDto eliminarEquipo(String equipoUuid, String username);
}
