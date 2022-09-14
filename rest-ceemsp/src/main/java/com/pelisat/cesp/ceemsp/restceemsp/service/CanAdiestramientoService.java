package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;

import java.util.List;

public interface CanAdiestramientoService {
    List<CanAdiestramientoDto> obtenerAdiestramientosPorCanUuid(String empresaUuid, String canUuid);
    List<CanAdiestramientoDto> obtenerTodosAdiestramientosPorCanUuid(String empresaUuid, String canUuid);
    CanAdiestramientoDto guardarCanAdiestramiento(String empresaUuid, String canUuid, String username, CanAdiestramientoDto canAdiestramientoDto);
    CanAdiestramientoDto modificarCanAdiestramiento(String empresaUuid, String canUuid, String adiestramientoUuid, String username, CanAdiestramientoDto canAdiestramientoDto);
    CanAdiestramientoDto eliminarCanAdiestramiento(String empresaUuid, String canUuid, String adiestramientoUuid, String username);
}
