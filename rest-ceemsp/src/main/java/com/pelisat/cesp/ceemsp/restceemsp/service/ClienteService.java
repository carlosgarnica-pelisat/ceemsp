package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;

import java.util.List;

public interface ClienteService {
    List<ClienteDto> obtenerClientesPorEmpresa(String empresaUuid);
    ClienteDto obtenerClientePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    ClienteDto crearCliente(String empresaUuid, String username, ClienteDto clienteDto);
}