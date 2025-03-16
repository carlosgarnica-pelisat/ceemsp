package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDto;

import java.util.List;

public interface BuzonSalidaService {
    List<BuzonInternoDto> obtenerTodosLosMensajes();
    BuzonInternoDto obtenerBuzonInternoPorUuid(String uuid);
    BuzonInternoDto guardarBuzonInterno(BuzonInternoDto buzonInternoDto, String username);
    BuzonInternoDto modificarBuzonInterno(String uuid, BuzonInternoDto buzonInternoDto, String username);
    BuzonInternoDto eliminarBuzonInterno(String uuid, String username);
}
