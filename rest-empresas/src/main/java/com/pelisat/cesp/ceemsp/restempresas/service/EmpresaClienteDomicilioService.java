package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;

import java.util.List;

public interface EmpresaClienteDomicilioService {
    List<ClienteDomicilioDto> obtenerDomiciliosPorCliente(int clienteId);
    List<ClienteDomicilioDto> obtenerDomiciliosPorClienteUuid(String empresaUuid, String clienteUuid);
    List<ClienteDomicilioDto> crearDomicilio(String username, String empresaUuid, String clienteUuid, List<ClienteDomicilioDto> clienteDomicilioDto);
    ClienteDomicilioDto obtenerPorId(Integer id);
    ClienteDomicilioDto modificarDomicilio(String empresaUuid, String clienteUuid, String domicilioUuid, String username, ClienteDomicilioDto clienteDomicilioDto);
    ClienteDomicilioDto eliminarDomicilio(String empresaUuid, String clienteUuid, String domicilioUuid, String username);
}
