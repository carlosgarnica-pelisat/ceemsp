package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoUsoDto;

import java.util.List;

public interface VehiculoUsoService {
    List<VehiculoUsoDto> obtenerTodos();
    VehiculoUsoDto obtenerPorUuid(String uuid);
    VehiculoUsoDto obtenerPorId(Integer id);
    VehiculoUsoDto crearNuevo(VehiculoUsoDto VehiculoUsoDto, String username);
    VehiculoUsoDto modificar(VehiculoUsoDto VehiculoUsoDto, String uuid, String username);
    VehiculoUsoDto eliminar(String uuid, String username);
}
