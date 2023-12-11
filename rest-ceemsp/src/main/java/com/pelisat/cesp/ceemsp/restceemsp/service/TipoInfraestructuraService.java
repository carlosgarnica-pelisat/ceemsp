package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.TipoInfraestructuraDto;

import java.util.List;

public interface TipoInfraestructuraService {
    List<TipoInfraestructuraDto> obtenerTiposInfraestructura();
    TipoInfraestructuraDto obtenerTipoInfraestructuraPorUuid(String uuid);
    TipoInfraestructuraDto obtenerTipoInfraestructuraPorId(Integer id);
    TipoInfraestructuraDto guardarTipoInfraestructura(TipoInfraestructuraDto tipoInfraestructuraDto, String username);
    TipoInfraestructuraDto modificarTipoInfraestructura(String uuid, TipoInfraestructuraDto tipoInfraestructuraDto, String username);
    TipoInfraestructuraDto eliminarTipoInfraestructura(String uuid, String username);
}
