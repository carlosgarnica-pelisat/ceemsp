package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;

public interface IncidenciaVehiculoService {
    VehiculoDto agregarVehiculoIncidencia(String empresaUuid, String incidenciaUuid, String username, VehiculoDto vehiculoDto);
    VehiculoDto eliminarVehiculoIncidencia(String empresaUuid, String incidenciaUuid, String vehiculoIncidenciaUuid, String username);
}
