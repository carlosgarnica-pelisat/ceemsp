package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteAsignacionPersonalDto;

import java.util.List;

public interface EmpresaClienteAsignacionPersonalService {
    List<ClienteAsignacionPersonalDto> obtenerAsignacionesCliente(String username, String clienteUuid);
    ClienteAsignacionPersonalDto obtenerAsignacionPorUuid(String clienteUuid, String asignacionUuid);
    ClienteAsignacionPersonalDto crearAsignacion(String clienteUuid, String username, ClienteAsignacionPersonalDto clienteAsignacionPersonalDto);
    ClienteAsignacionPersonalDto modificarAsignacion(String clienteUuid, String asignacionUuid, String username, ClienteAsignacionPersonalDto clienteAsignacionPersonalDto);
    ClienteAsignacionPersonalDto eliminarAsignacion(String clienteUuid, String asignacionUUid, String username);
}
