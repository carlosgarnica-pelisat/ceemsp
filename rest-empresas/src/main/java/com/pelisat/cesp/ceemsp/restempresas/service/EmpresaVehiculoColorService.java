package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoColorDto;

import java.util.List;

public interface EmpresaVehiculoColorService {
    List<VehiculoColorDto> obtenerTodosPorVehiculoUuid(String vehiculoUuid);
    VehiculoColorDto guardarcolor(String vehiculoUuid, String username, VehiculoColorDto vehiculoColorDto);
    VehiculoColorDto modificarColor(String vehiculoUuid, String colorUuid, String username, VehiculoColorDto vehiculoColorDto);
    VehiculoColorDto eliminarColor(String vehiculoUuid, String colorUuid, String username);
}
