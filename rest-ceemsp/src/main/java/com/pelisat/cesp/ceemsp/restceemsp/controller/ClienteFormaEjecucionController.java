package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteFormaEjecucionService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClienteFormaEjecucionController {
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_FORMA_EJECUCION_URI = "/empresas/{empresaUuid}/clientes/{clienteUuid}/formas-ejecucion";
    private final ClienteFormaEjecucionService clienteFormaEjecucionService;

    @Autowired
    public ClienteFormaEjecucionController(JwtUtils jwtUtils, ClienteFormaEjecucionService clienteFormaEjecucionService) {
        this.clienteFormaEjecucionService = clienteFormaEjecucionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CLIENTE_FORMA_EJECUCION_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteFormaEjecucionDto> obtenerClienteFormasEjecucion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) {
        return clienteFormaEjecucionService.obtenerFormasEjecucionPorClienteUuid(empresaUuid, clienteUuid);
    }

    @PostMapping(value = CLIENTE_FORMA_EJECUCION_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteFormaEjecucionDto guardarClienteDomicilio(
            @RequestBody ClienteFormaEjecucionDto clienteFormaEjecucionDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteFormaEjecucionService.crearFormaEjecucion(username, empresaUuid, clienteUuid, clienteFormaEjecucionDto);
    }

    @PutMapping(value = CLIENTE_FORMA_EJECUCION_URI + "/{formaEjecucionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteFormaEjecucionDto modificarClienteFormaEjecucion(
            @RequestBody ClienteFormaEjecucionDto clienteFormaEjecucionDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "formaEjecucionUuid") String formaEjecucionUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteFormaEjecucionService.modificarFormaEjecucion(empresaUuid, clienteUuid, formaEjecucionUuid, username, clienteFormaEjecucionDto);
    }

    @DeleteMapping(value = CLIENTE_FORMA_EJECUCION_URI + "/{formaEjecucionUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteFormaEjecucionDto eliminarClienteFormaEjecucion(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteFormaEjecucionService.eliminarFormaEjecucion(empresaUuid, clienteUuid, domicilioUuid, username);
    }
}
