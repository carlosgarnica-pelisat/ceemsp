package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.CanDto;

public interface IncidenciaCanService {
    CanDto agregarCanIncidencia(String empresaUuid, String incidenciaUuid, String username, CanDto canDto);
    CanDto eliminarCanIncidencia(String empresaUuid, String incidenciaUuid, String canIncidenciaUuid, String username);
}
