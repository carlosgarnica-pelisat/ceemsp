package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanTipoAdiestramientoDto;

import java.util.List;

public interface CanTipoAdiestramientoService {
    List<CanTipoAdiestramientoDto> obtenerTodos();

    CanTipoAdiestramientoDto obtenerPorUuid(String uuid);

    CanTipoAdiestramientoDto obtenerPorId(Integer id);

    CanTipoAdiestramientoDto crearNuevo(CanTipoAdiestramientoDto canTipoAdiestramientoDto, String username);

    CanTipoAdiestramientoDto modificar(CanTipoAdiestramientoDto canTipoAdiestramientoDto, String uuid, String username);

    CanTipoAdiestramientoDto eliminar(String uuid, String username);
}
