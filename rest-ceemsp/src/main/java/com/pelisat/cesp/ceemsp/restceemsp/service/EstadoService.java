package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ColoniaDto;
import com.pelisat.cesp.ceemsp.database.dto.EstadoDto;
import com.pelisat.cesp.ceemsp.database.dto.LocalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.MunicipioDto;

import java.util.List;

public interface EstadoService {
    List<EstadoDto> obtenerTodosLosEstados();
    EstadoDto obtenerPorId(int estadoId);
    List<MunicipioDto> obtenerMunicipiosPorEstadoUuid(String uuidEstado);
    List<LocalidadDto> obtenerLocalidadesPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid);
    List<ColoniaDto> obtenerColoniasPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid);
}
