package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteAsignacionPersonalDto;

import java.util.List;

public interface ClienteAsignacionPersonalService {
    List<ClienteAsignacionPersonalDto> obtenerAsignacionesCliente(String empresaUuid, String clienteUuid);
    List<ClienteAsignacionPersonalDto> obtenerAsignacionesClienteTodo(String empresaUuid, String clienteUuid);
    ClienteAsignacionPersonalDto obtenerAsignacionPorUuid(String empresaUuid, String clienteUuid, String asignacionUuid);
    ClienteAsignacionPersonalDto crearAsignacion(String empresaUUid, String clienteUuid, String username, ClienteAsignacionPersonalDto clienteAsignacionPersonalDto);
    ClienteAsignacionPersonalDto modificarAsignacion(String empresaUuid, String clienteUuid, String asignacionUuid, String username, ClienteAsignacionPersonalDto clienteAsignacionPersonalDto);
    ClienteAsignacionPersonalDto eliminarAsignacion(String empresaUuid, String clienteUuid, String asignacionUUid, String username);
}
