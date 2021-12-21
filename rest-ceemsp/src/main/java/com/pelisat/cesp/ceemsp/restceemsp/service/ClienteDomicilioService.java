package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.SubmodalidadDto;

import java.util.List;

public interface ClienteDomicilioService {
    List<ClienteDomicilioDto> obtenerDomiciliosPorCliente(int clienteId);

    List<ClienteDomicilioDto> obtenerDomiciliosPorClienteUuid(String empresaUuid, String clienteUuid);

    List<ClienteDomicilioDto> crearDomicilio(String username, String empresaUuid, String clienteUuid, List<ClienteDomicilioDto> clienteDomicilioDto);

    ClienteDomicilioDto obtenerPorId(Integer id);
}
