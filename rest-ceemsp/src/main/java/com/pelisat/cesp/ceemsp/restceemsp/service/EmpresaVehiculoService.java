package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalVehiculoDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaVehiculoService {
    List<VehiculoDto> obtenerVehiculosPorEmpresa(String empresaUuid);
    List<VehiculoDto> obtenerVehiculosEliminadosPorEmpresa(String empresaUuid);
    List<VehiculoDto> obtenerVehiculosInstalacionesPorEmpresa(String empresaUuid);
    VehiculoDto obtenerVehiculoPorUuid(String empresaUuid, String vehiculoUuid, boolean soloEntidad);
    File obtenerConstanciaBlindaje(String empresaUuid, String vehiculoUuid);
    File descargarDocumentoFundatorio(String empresaUuid, String vehiculoUuid);
    VehiculoDto obtenerVehiculoPorId(String empresaUuid, Integer vehiculoId);
    VehiculoDto guardarVehiculo(String empresaUuid, String username, VehiculoDto vehiculoDto, MultipartFile constanciaBlindaje);
    VehiculoDto modificarVehiculo(String empresaUuid, String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile multipartFile);
    VehiculoDto eliminarVehiculo(String empresaUuid, String vehiculoUuid, String username, VehiculoDto vehiculoDto, MultipartFile multipartFile);
    List<PersonalVehiculoDto> obtenerMovimientosVehiculoPorUuid(String empresaUuid, String vehiculoUuid);
    List<VehiculoDomicilioDto> obtenerMovimientosDomiciliosVehiculo(String empresaUuid, String vehiculoUuid);
}
