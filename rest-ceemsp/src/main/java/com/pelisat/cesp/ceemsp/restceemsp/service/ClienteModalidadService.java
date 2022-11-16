package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteModalidadDto;

import java.util.List;

public interface ClienteModalidadService {
    List<ClienteModalidadDto> obtenerModalidadesPorCliente(String uuid, String clienteUuid);
    ClienteModalidadDto guardarModalidadCliente(String uuid, String clienteUuid, String username, ClienteModalidadDto clienteModalidadDto);
    ClienteModalidadDto modificarModalidadCliente(String uuid, String clienteUuid, String modalidadUuid, String username, ClienteModalidadDto clienteModalidadDto);
    ClienteModalidadDto eliminarModalidad(String uuid, String clienteUuid, String modalidadUuid, String username);
}
