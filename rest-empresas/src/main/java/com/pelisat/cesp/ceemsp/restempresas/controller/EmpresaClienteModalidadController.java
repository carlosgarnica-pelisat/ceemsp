package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteModalidadDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaClienteModalidadService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaClienteModalidadController {
    private final EmpresaClienteModalidadService empresaClienteModalidadService;
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_MODALIDADES_PERSONAL_URI =  "/clientes/{clienteUuid}/modalidades";

    @Autowired
    public EmpresaClienteModalidadController(
            EmpresaClienteModalidadService empresaClienteModalidadService, JwtUtils jwtUtils
    ) {
        this.empresaClienteModalidadService = empresaClienteModalidadService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteModalidadDto> obtenerAsignacionesPorCliente(
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteModalidadService.obtenerModalidadesPorCliente(username, clienteUuid);
    }

    @PostMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteModalidadDto guardarAsignacion(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @RequestBody ClienteModalidadDto clienteModalidadDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteModalidadService.guardarModalidadCliente(clienteUuid, username, clienteModalidadDto);
    }

    @PutMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI + "/{modalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteModalidadDto modificarAsignacion(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "modalidadUuid") String asignacionUuid,
            @RequestBody ClienteModalidadDto clienteModalidadDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteModalidadService.modificarModalidadCliente(clienteUuid, asignacionUuid, username, clienteModalidadDto);
    }

    @DeleteMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI + "/{modalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteModalidadDto eliminarAsignacion(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "modalidadUuid") String modalidadUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteModalidadService.eliminarModalidad(clienteUuid, modalidadUuid, username);
    }
}
