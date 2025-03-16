package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaClaseDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;
import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;

import java.util.List;

public interface VehiculoMarcaService {
    List<VehiculoMarcaDto> obtenerTodos();
    List<VehiculoMarcaDto> obtenerMarcaTipo(VehiculoTipoEnum vehiculoTipoEnum);
    VehiculoMarcaDto obtenerPorUuid(String uuid);
    VehiculoMarcaDto obtenerPorId(Integer id);
    VehiculoMarcaDto crearNuevo(VehiculoMarcaDto vehiculoMarcaDto, String username);
    VehiculoMarcaDto modificar(VehiculoMarcaDto vehiculoMarcaDto, String uuid, String username);
    VehiculoMarcaDto eliminar(String uuid, String username);
}
