package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoSubmarcaDto;
import com.pelisat.cesp.ceemsp.database.model.VehiculoSubmarca;

import java.util.List;

public interface VehiculoSubmarcaService {
    List<VehiculoSubmarcaDto> obtenerTodos();
    VehiculoSubmarcaDto obtenerPorUuid(String uuid);
    VehiculoSubmarcaDto obtenerPorId(Integer id);
    VehiculoSubmarcaDto crearNuevo(VehiculoSubmarcaDto vehiculoSubmarcaDto, String username);
    VehiculoSubmarcaDto modificar(VehiculoSubmarcaDto vehiculoSubmarcaDto, String uuid, String username);
    VehiculoSubmarcaDto eliminar(String uuid, String username);
}
