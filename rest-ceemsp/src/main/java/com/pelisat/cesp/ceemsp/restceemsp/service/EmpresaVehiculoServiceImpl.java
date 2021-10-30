package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaVehiculoServiceImpl implements EmpresaVehiculoService {

    @Override
    public List<VehiculoDto> obtenerVehiculosPorEmpresa(String empresaUuid) {
        return null;
    }

    @Override
    public VehiculoDto obtenerVehiculoPorUuid(String empresaUuid, String vehiculoUuid, boolean soloEntidad) {
        return null;
    }

    @Override
    public VehiculoDto guardarVehiculo(String empresaUuid, String username, VehiculoDto vehiculoDto) {
        return null;
    }
}
