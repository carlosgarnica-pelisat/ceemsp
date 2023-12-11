package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaVehiculoService {
    List<VehiculoDto> obtenerVehiculos(String empresaUsername);
    List<VehiculoDto> obtenerVehiculosEnInstalacionesPorEmpresa(String username);
    VehiculoDto obtenerVehiculoPorUuid(String vehiculoUuid);
    VehiculoDto obtenerVehiculoPorId(Integer id);
    VehiculoDto guardarVehiculo(String username, VehiculoDto vehiculoDto,  MultipartFile constanciaBlindaje);
    VehiculoDto modificarVehiculo(String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile constanciaBlindaje);
    VehiculoDto eliminarVehiculo(String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile multipartFile);
    File obtenerConstanciaBlindaje(String vehiculoUuid);
}
