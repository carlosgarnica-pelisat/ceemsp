package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaMarcaDto;

import java.util.List;

public interface ArmaMarcaService {
    List<ArmaMarcaDto> obtenerTodos();

    ArmaMarcaDto obtenerPorUuid(String uuid);

    ArmaMarcaDto obtenerPorId(Integer id);

    ArmaMarcaDto crearNuevo(ArmaMarcaDto armaMarcaDto, String username);

    ArmaMarcaDto modificar(ArmaMarcaDto armaMarcaDto, String uuid, String username);

    ArmaMarcaDto eliminar(String uuid, String username);
}
