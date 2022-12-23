package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteFormaEjecucionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteFormaEjecucionServiceImpl implements ClienteFormaEjecucionService {



    @Override
    public List<ClienteFormaEjecucionDto> obtenerFormasEjecucionPorClienteUuid(String uuid, String clienteUuid) {
        return null;
    }

    @Override
    public ClienteFormaEjecucionDto crearFormaEjecucion(String uuid, String clienteUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto) {
        return null;
    }

    @Override
    public ClienteFormaEjecucionDto modificarFormaEjecucion(String uuid, String clienteUuid, String formaEjecucionUuid, String username, ClienteFormaEjecucionDto clienteFormaEjecucionDto) {
        return null;
    }

    @Override
    public ClienteFormaEjecucionDto eliminarFormaEjecucion(String uuid, String clienteUuid, String formaEjecucionUuid, String username) {
        return null;
    }
}
