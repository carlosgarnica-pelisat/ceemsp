package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ColoniaDto;

public interface ColoniaService {
    ColoniaDto obtenerColoniaPorId(int id);
    ColoniaDto obtenerColoniaPorUuid(String uuid);
}
