package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;

import java.util.List;

public interface CanService {
    List<CanDto> obtenerCanesPorEmpresa(String empresaUuid);
    CanDto obtenerCanPorUuid(String empresaUuid, String canUuid, boolean soloEntidad);
    CanDto obtenerCanPorId(int id);
    CanDto guardarCan(String empresaUuid, String username, CanDto canDto);
}
