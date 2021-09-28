package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;

import java.util.List;

public interface ModalidadService {
    List<ModalidadDto> obtenerModalidades();

    ModalidadDto obtenerModalidadPorUuid(String uuid);

    ModalidadDto obtenerModalidadPorId(Integer id);

    ModalidadDto guardarModalidad(ModalidadDto modalidadDto, String username);

    ModalidadDto modificarModalidad(ModalidadDto modalidadDto, String uuid, String username);

    ModalidadDto eliminarModalidad(String uuid, String username);
}
