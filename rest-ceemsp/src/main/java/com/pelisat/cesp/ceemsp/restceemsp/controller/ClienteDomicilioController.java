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
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return clienteDomicilioService.obtenerDomiciliosPorClienteUuid(empresaUuid);
    }

    @PostMapping(value = CLIENTE_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDomicilioDto> guardarClienteDomicilio(
            @RequestBody List<ClienteDomicilioDto> clienteDomicilioDtos,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteDomicilioService.crearDomicilio(empresaUuid, username, clienteDomicilioDtos);
    }
}
