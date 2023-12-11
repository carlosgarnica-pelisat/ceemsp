package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.RealizarBusquedaDto;
import com.pelisat.cesp.ceemsp.database.dto.ResultadosBusquedaDto;

public interface BusquedaService {
    ResultadosBusquedaDto realizarBusqueda(RealizarBusquedaDto busquedaDto);
}
