package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;

import java.util.List;

public interface EmpresaVehiculoService {
    List<VehiculoDto> obtenerVehiculosPorEmpresa(String empresaUuid);

    VehiculoDto obtenerVehiculoPorUuid(String empresaUuid, String vehiculoUuid, boolean soloEntidad);

    VehiculoDto guardarVehiculo(String empresaUuid, String username, VehiculoDto vehiculoDto);
}
