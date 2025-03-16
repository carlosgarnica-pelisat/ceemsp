package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;

public interface EmpresaIncidenciaVehiculoService {
    VehiculoDto agregarVehiculoIncidencia(String incidenciaUuid, String username, VehiculoDto vehiculoDto);
    VehiculoDto eliminarVehiculoIncidencia(String incidenciaUuid, String vehiculoIncidenciaUuid, String username);
}
