package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaClienteFormaEjecucionService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaClienteFormaEjecucionController {
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_FORMA_EJECUCION_URI = "/clientes/{clienteUuid}/formas-ejecucion";
    private final EmpresaClienteFormaEjecucionService empresaClienteFormaEjecucionService;

    @Autowired
    public EmpresaClienteFormaEjecucionController(JwtUtils jwtUtils, EmpresaClienteFormaEjecucionService empresaClienteFormaEjecucionService) {
        this.empresaClienteFormaEjecucionService = empresaClienteFormaEjecucionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CLIENTE_FORMA_EJECUCION_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteFormaEjecucionDto> obtenerClienteFormasEjecucion(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteFormaEjecucionService.obtenerFormasEjecucionPorClienteUuid(username, clienteUuid);
    }

    @PostMapping(value = CLIENTE_FORMA_EJECUCION_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteFormaEjecucionDto guardarClienteDomicilio(
            @RequestBody ClienteFormaEjecucionDto clienteFormaEjecucionDto,
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteFormaEjecucionService.crearFormaEjecucion(clienteUuid, username, clienteFormaEjecucionDto);
    }

    @PutMapping(value = CLIENTE_FORMA_EJECUCION_URI + "/{formaEjecucionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteFormaEjecucionDto modificarClienteFormaEjecucion(
            @RequestBody ClienteFormaEjecucionDto clienteFormaEjecucionDto,
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "formaEjecucionUuid") String formaEjecucionUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteFormaEjecucionService.modificarFormaEjecucion(clienteUuid, formaEjecucionUuid, username, clienteFormaEjecucionDto);
    }

    @DeleteMapping(value = CLIENTE_FORMA_EJECUCION_URI + "/{formaEjecucionUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteFormaEjecucionDto eliminarClienteFormaEjecucion(
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "formaEjecucionUuid") String formaEjecucionUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteFormaEjecucionService.eliminarFormaEjecucion(clienteUuid, formaEjecucionUuid, username);
    }
}
