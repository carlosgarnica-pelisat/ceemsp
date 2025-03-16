package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.database.model.ClienteFormaEjecucion;

import java.util.List;

public interface ClienteFormaEjecucionService {
    List<ClienteFormaEjecucionDto> obtenerFormasEjecucionPorClienteUuid(String uuid, String clienteUuid);
    ClienteFormaEjecucionDto crearFormaEjecucion(String uuid, String clienteUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto);
    ClienteFormaEjecucionDto modificarFormaEjecucion(String uuid, String clienteUuid, String formaEjecucionUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto);
    ClienteFormaEjecucionDto eliminarFormaEjecucion(String uuid, String clienteUuid, String formaEjecucionUuid, String username);
}
