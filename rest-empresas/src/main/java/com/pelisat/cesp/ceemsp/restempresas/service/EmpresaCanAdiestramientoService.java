package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;

import java.util.List;

public interface EmpresaCanAdiestramientoService {
    List<CanAdiestramientoDto> obtenerAdiestramientosPorCanUuid(String canUuid);
    CanAdiestramientoDto guardarCanAdiestramiento(String canUuid, String username, CanAdiestramientoDto canAdiestramientoDto);
    CanAdiestramientoDto modificarCanAdiestramiento(String canUuid, String adiestramientoUuid, String username, CanAdiestramientoDto canAdiestramientoDto);
    CanAdiestramientoDto eliminarCanAdiestramiento(String canUuid, String adiestramientoUuid, String username);
}
