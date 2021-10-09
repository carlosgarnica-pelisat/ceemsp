package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaClaseDto;
import com.pelisat.cesp.ceemsp.database.dto.CanRazaDto;

import java.util.List;

public interface ArmaClaseService {
    List<ArmaClaseDto> obtenerTodos();

    ArmaClaseDto obtenerPorUuid(String uuid);

    ArmaClaseDto obtenerPorId(Integer id);

    ArmaClaseDto crearNuevo(ArmaClaseDto armaClaseDto, String username);

    ArmaClaseDto modificar(ArmaClaseDto armaClaseDto, String uuid, String username);

    ArmaClaseDto eliminar(String uuid, String username);
}
