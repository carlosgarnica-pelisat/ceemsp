package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EquipoDto;

import java.util.List;

public interface EquipoService {
    List<EquipoDto> obtenerEquipos();
    List<EquipoDto> obtenerEquipos(String empresaUuid);
    EquipoDto obtenerEquipoPorUuid(String equipoUuid);
    EquipoDto obtenerEquipoPorId(int id);
    EquipoDto guardarEquipo(EquipoDto equipoDto, String username);
    EquipoDto modificarEquipo(String equipoUuid, String username, EquipoDto equipoDto);
    EquipoDto eliminarEquipo(String equipoUuid, String username);
}
