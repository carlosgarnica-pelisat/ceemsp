package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.LocalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.MunicipioDto;

public interface LocalidadService {
    LocalidadDto obtenerLocalidadPorId(int id);
    LocalidadDto obtenerLocalidadPorUuid(String uuid);
}
