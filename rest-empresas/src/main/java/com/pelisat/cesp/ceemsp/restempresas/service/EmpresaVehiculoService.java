package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;

import java.util.List;

public interface EmpresaVehiculoService {
    List<VehiculoDto> obtenerVehiculos(String empresaUsername);
    VehiculoDto obtenerVehiculoPorUuid(String empresaUsername, String vehiculoUuid);
    VehiculoDto guardarVehiculo(String empresaUsername, VehiculoDto vehiculoDto);
}
