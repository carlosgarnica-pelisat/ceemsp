package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;

import java.util.List;

public interface CanCartillaVacunacionService {
    List<CanCartillaVacunacionDto> obtenerCartillasVacunacionPorCanUuid(String empresaUuid, String canUuid);
    CanCartillaVacunacionDto guardarCartillaVacunacion(String empresaUuid, String canUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto);
}
