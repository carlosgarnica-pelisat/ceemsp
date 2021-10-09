package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoTipoDto;

import java.util.List;

public interface VehiculoTipoService {
    List<VehiculoTipoDto> obtenerTodos();

    VehiculoTipoDto obtenerPorUuid(String uuid);

    VehiculoTipoDto obtenerPorId(Integer id);

    VehiculoTipoDto crearNuevo(VehiculoTipoDto vehiculoTipoDto, String username);

    VehiculoTipoDto modificar(VehiculoTipoDto vehiculoTipoDto, String uuid, String username);

    VehiculoTipoDto eliminar(String uuid, String username);
}
