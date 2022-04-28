package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaVehiculoService {
    List<VehiculoDto> obtenerVehiculosPorEmpresa(String empresaUuid);
    VehiculoDto obtenerVehiculoPorUuid(String empresaUuid, String vehiculoUuid, boolean soloEntidad);
    VehiculoDto obtenerVehiculoPorId(String empresaUuid, Integer vehiculoId);
    VehiculoDto guardarVehiculo(String empresaUuid, String username, VehiculoDto vehiculoDto);
    VehiculoDto modificarVehiculo(String empresaUuid, String vehiculoUuid, String username, VehiculoDto vehiculoDto);
    VehiculoDto eliminarVehiculo(String empresaUuid, String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile multipartFile);
}
