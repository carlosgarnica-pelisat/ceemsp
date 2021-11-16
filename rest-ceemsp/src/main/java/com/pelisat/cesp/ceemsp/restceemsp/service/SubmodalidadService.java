package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.SubmodalidadDto;

import java.util.List;

public interface SubmodalidadService {
    List<SubmodalidadDto> obtenerSubmodalidadesPorModalidad(int modalidadId);

    SubmodalidadDto obtenerSubmodalidadPorId(int modalidadId);
}
