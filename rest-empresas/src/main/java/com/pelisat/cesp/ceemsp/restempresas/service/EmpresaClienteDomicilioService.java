package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;

import java.util.List;

public interface EmpresaClienteDomicilioService {
    List<ClienteDomicilioDto> obtenerDomiciliosPorCliente(int clienteId);
    List<ClienteDomicilioDto> obtenerDomiciliosPorClienteUuid(String clienteUuid);
    ClienteDomicilioDto crearDomicilio(String username, String clienteUuid, ClienteDomicilioDto clienteDomicilioDto);
    ClienteDomicilioDto obtenerPorId(Integer id);
    ClienteDomicilioDto modificarDomicilio(String clienteUuid, String domicilioUuid, String username, ClienteDomicilioDto clienteDomicilioDto);
    ClienteDomicilioDto cambiarMatriz(String clienteUuid, String domicilioUuid, String username);
    ClienteDomicilioDto eliminarDomicilio(String clienteUuid, String domicilioUuid, String username);
}
