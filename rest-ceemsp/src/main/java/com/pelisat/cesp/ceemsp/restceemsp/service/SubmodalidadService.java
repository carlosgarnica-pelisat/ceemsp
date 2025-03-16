package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.SubmodalidadDto;

import java.util.List;

public interface SubmodalidadService {
    List<SubmodalidadDto> obtenerSubmodalidadesPorModalidad(int modalidadId);
    List<SubmodalidadDto> obtenerSubmodalidadesPorModalidadUuid(String uuid);
    SubmodalidadDto obtenerSubmodalidadPorId(int modalidadId);
    SubmodalidadDto guardarSubmodalidad(String modalidadUuid, String username, SubmodalidadDto submodalidadDto);
    SubmodalidadDto modificarSubmodalidad(String modalidadUuid, String submodalidadUuid, String username, SubmodalidadDto submodalidadDto);
    SubmodalidadDto eliminarSubmodalidad(String modalidadUuid, String submodalidadUuid, String username);
}
