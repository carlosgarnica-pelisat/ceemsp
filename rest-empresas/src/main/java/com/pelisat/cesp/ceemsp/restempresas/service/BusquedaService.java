package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.RealizarBusquedaDto;
import com.pelisat.cesp.ceemsp.database.dto.ResultadosBusquedaDto;

public interface BusquedaService {
    ResultadosBusquedaDto realizarBusqueda(RealizarBusquedaDto busquedaDto, String username);
}
