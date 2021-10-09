package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoTipoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoTipoServiceImpl implements VehiculoTipoService {
    @Override
    public List<VehiculoTipoDto> obtenerTodos() {
        return null;
    }

    @Override
    public VehiculoTipoDto obtenerPorUuid(String uuid) {
        return null;
    }

    @Override
    public VehiculoTipoDto obtenerPorId(Integer id) {
        return null;
    }

    @Override
    public VehiculoTipoDto crearNuevo(VehiculoTipoDto vehiculoTipoDto, String username) {
        return null;
    }

    @Override
    public VehiculoTipoDto modificar(VehiculoTipoDto vehiculoTipoDto, String uuid, String username) {
        return null;
    }

    @Override
    public VehiculoTipoDto eliminar(String uuid, String username) {
        return null;
    }
}
