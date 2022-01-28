package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaVehiculoServiceImpl implements EmpresaVehiculoService {
    @Override
    public List<VehiculoDto> obtenerVehiculos(String empresaUsername) {
        return null;
    }

    @Override
    public VehiculoDto obtenerVehiculoPorUuid(String empresaUsername, String vehiculoUuid) {
        return null;
    }

    @Override
    public VehiculoDto guardarVehiculo(String empresaUsername, VehiculoDto vehiculoDto) {
        return null;
    }
}
