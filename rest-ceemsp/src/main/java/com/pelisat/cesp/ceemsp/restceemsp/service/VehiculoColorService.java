package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoColorDto;

import java.util.List;

public interface VehiculoColorService {
    List<VehiculoColorDto> obtenerTodosPorVehiculoUuid(String vehiculoUuid, String empresaUuid);
    List<VehiculoColorDto> obtenerTodosPorVehiculoId(int id);
    VehiculoColorDto guardarcolor(String empresaUuid, String vehiculoUuid, String username, VehiculoColorDto vehiculoColorDto);
}
