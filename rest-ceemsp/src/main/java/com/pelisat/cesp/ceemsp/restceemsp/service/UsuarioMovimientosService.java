package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.MovimientoDto;
import com.pelisat.cesp.ceemsp.database.type.TipoMovimientoUsuarioEnum;

import java.util.List;

public interface UsuarioMovimientosService {
    List<MovimientoDto> obtenerMovimientos(String usuarioUuid, TipoMovimientoUsuarioEnum tipoMovimiento);
}
