package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanRazaDto;

import java.util.List;

public interface CanRazaService {
    List<CanRazaDto> obtenerTodos();

    CanRazaDto obtenerPorUuid(String uuid);

    CanRazaDto obtenerPorId(Integer id);

    CanRazaDto crearNuevo(CanRazaDto canRazaDto, String username);

    CanRazaDto modificar(CanRazaDto canRazaDto, String uuid, String username);

    CanRazaDto eliminar(String uuid, String username);
}
