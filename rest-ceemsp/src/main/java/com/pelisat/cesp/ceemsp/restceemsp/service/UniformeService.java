package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.UniformeDto;

import java.util.List;

public interface UniformeService {
    List<UniformeDto> obtenerUniformes();
    UniformeDto obtenerUniformePorUuid(String uniformeUuid);
    UniformeDto guardarUniforme(UniformeDto uniformeDto, String username);
    UniformeDto modificarUniforme(String uniformeUuid, String username, UniformeDto uniformeDto);
    UniformeDto eliminarUniforme(String uniformeUuid, String username);
}
