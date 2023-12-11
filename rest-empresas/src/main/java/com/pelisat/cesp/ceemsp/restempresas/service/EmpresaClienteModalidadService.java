package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteModalidadDto;

import java.util.List;

public interface EmpresaClienteModalidadService {
    List<ClienteModalidadDto> obtenerModalidadesPorCliente(String username, String clienteUuid);
    ClienteModalidadDto guardarModalidadCliente(String clienteUuid, String username, ClienteModalidadDto clienteModalidadDto);
    ClienteModalidadDto modificarModalidadCliente(String clienteUuid, String modalidadUuid, String username, ClienteModalidadDto clienteModalidadDto);
    ClienteModalidadDto eliminarModalidad(String clienteUuid, String modalidadUuid, String username);
}
