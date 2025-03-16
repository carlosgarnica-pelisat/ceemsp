package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteDomicilioService;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClienteDomicilioController {
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_DOMICILIOS_URI = "/empresas/{empresaUuid}/clientes/{clienteUuid}/domicilios";
    private final ClienteDomicilioService clienteDomicilioService;

    @Autowired
    public ClienteDomicilioController(JwtUtils jwtUtils, ClienteDomicilioService clienteDomicilioService) {
        this.clienteDomicilioService = clienteDomicilioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CLIENTE_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDomicilioDto> obtenerClienteDomicilioPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) {
        return clienteDomicilioService.obtenerDomiciliosPorClienteUuid(empresaUuid, clienteUuid);
    }

    @PostMapping(value = CLIENTE_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDomicilioDto guardarClienteDomicilio(
            @RequestBody ClienteDomicilioDto clienteDomicilioDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteDomicilioService.crearDomicilio(username, empresaUuid, clienteUuid, clienteDomicilioDto);
    }

    @PutMapping(value = CLIENTE_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDomicilioDto modificarClienteDomicilio(
            @RequestBody ClienteDomicilioDto clienteDomicilioDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteDomicilioService.modificarDomicilio(empresaUuid, clienteUuid, domicilioUuid, username, clienteDomicilioDto);
    }

    @DeleteMapping(value = CLIENTE_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDomicilioDto eliminarClienteDomicilio(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteDomicilioService.eliminarDomicilio(empresaUuid, clienteUuid, domicilioUuid, username);
    }
}
