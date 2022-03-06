package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.MunicipioDto;

public interface MunicipioService {
    MunicipioDto obtenerMunicipioPorId(int id);
    MunicipioDto obtenerMunicipioPorUuid(String uuid);
}
