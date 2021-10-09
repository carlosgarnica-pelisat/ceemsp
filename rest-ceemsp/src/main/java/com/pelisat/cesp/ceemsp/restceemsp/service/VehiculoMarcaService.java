package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaClaseDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;

import java.util.List;

public interface VehiculoMarcaService {
    List<VehiculoMarcaDto> obtenerTodos();

    VehiculoMarcaDto obtenerPorUuid(String uuid);

    VehiculoMarcaDto obtenerPorId(Integer id);

    VehiculoMarcaDto crearNuevo(VehiculoMarcaDto vehiculoMarcaDto, String username);

    VehiculoMarcaDto modificar(VehiculoMarcaDto vehiculoMarcaDto, String uuid, String username);

    VehiculoMarcaDto eliminar(String uuid, String username);
}
