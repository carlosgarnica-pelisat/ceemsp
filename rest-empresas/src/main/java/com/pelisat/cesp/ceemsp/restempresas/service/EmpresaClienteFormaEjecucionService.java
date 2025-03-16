package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteFormaEjecucionDto;

import java.util.List;

public interface EmpresaClienteFormaEjecucionService {
    List<ClienteFormaEjecucionDto> obtenerFormasEjecucionPorClienteUuid(String username, String clienteUuid);
    ClienteFormaEjecucionDto crearFormaEjecucion(String clienteUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto);
    ClienteFormaEjecucionDto modificarFormaEjecucion(String clienteUuid, String formaEjecucionUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto);
    ClienteFormaEjecucionDto eliminarFormaEjecucion(String clienteUuid, String formaEjecucionUuid, String username);
}
