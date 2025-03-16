package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaClienteDomicilioService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaClienteDomicilioController {
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_DOMICILIOS_URI = "/clientes/{clienteUuid}/domicilios";
    private final EmpresaClienteDomicilioService empresaClienteDomicilioService;

    @Autowired
    public EmpresaClienteDomicilioController(JwtUtils jwtUtils, EmpresaClienteDomicilioService empresaClienteDomicilioService) {
        this.empresaClienteDomicilioService = empresaClienteDomicilioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CLIENTE_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDomicilioDto> obtenerClienteDomicilioPorUuid(
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) {
        return empresaClienteDomicilioService.obtenerDomiciliosPorClienteUuid(clienteUuid);
    }

    @PostMapping(value = CLIENTE_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDomicilioDto guardarClienteDomicilio(
            @RequestBody ClienteDomicilioDto clienteDomicilioDto,
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteDomicilioService.crearDomicilio(username, clienteUuid, clienteDomicilioDto);
    }

    @PutMapping(value = CLIENTE_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDomicilioDto modificarClienteDomicilio(
            @RequestBody ClienteDomicilioDto clienteDomicilioDto,
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteDomicilioService.modificarDomicilio(clienteUuid, domicilioUuid, username, clienteDomicilioDto);
    }

    @PutMapping(value = CLIENTE_DOMICILIOS_URI + "/{domicilioUuid}/matriz", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDomicilioDto cambiarMatriz(
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteDomicilioService.cambiarMatriz(clienteUuid, domicilioUuid, username);
    }

    @DeleteMapping(value = CLIENTE_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDomicilioDto eliminarClienteDomicilio(
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteDomicilioService.eliminarDomicilio(clienteUuid, domicilioUuid, username);
    }
}
