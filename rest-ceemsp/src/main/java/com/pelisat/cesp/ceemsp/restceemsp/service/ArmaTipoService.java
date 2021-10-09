package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaTipoDto;

import java.util.List;

public interface ArmaTipoService {
    List<ArmaTipoDto> obtenerTodos();

    ArmaTipoDto obtenerPorUuid(String uuid);

    ArmaTipoDto obtenerPorId(Integer id);

    ArmaTipoDto crearNuevo(ArmaTipoDto armaTipoDto, String username);

    ArmaTipoDto modificar(ArmaTipoDto armaTipoDto, String uuid, String username);

    ArmaTipoDto eliminar(String uuid, String username);
}
