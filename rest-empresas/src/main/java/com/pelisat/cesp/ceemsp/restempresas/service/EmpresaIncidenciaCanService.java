package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;

public interface EmpresaIncidenciaCanService {
    CanDto agregarCanIncidencia(String incidenciaUuid, String username, CanDto canDto);
    CanDto eliminarCanIncidencia(String incidenciaUuid, String canIncidenciaUuid, String username);
}
