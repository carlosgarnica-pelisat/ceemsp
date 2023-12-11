package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteAsignacionPersonalDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaClienteAsignacionPersonalService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaClienteAsignacionPersonalController {
    private final EmpresaClienteAsignacionPersonalService empresaClienteAsignacionPersonalService;
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_ASIGNACION_PERSONAL_URI =  "/clientes/{clienteUuid}/asignaciones";

    @Autowired
    public EmpresaClienteAsignacionPersonalController(EmpresaClienteAsignacionPersonalService empresaClienteAsignacionPersonalService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaClienteAsignacionPersonalService = empresaClienteAsignacionPersonalService;
    }

    @GetMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteAsignacionPersonalDto> obtenerAsignacionesPorCliente(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteAsignacionPersonalService.obtenerAsignacionesCliente(username, clienteUuid);
    }

    @PostMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteAsignacionPersonalDto guardarAsignacion(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @RequestBody ClienteAsignacionPersonalDto clienteAsignacionPersonalDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteAsignacionPersonalService.crearAsignacion(clienteUuid, username, clienteAsignacionPersonalDto);
    }

    @PutMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI + "/{asignacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteAsignacionPersonalDto modificarAsignacion(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "asignacionUuid") String asignacionUuid,
            @RequestBody ClienteAsignacionPersonalDto clienteAsignacionPersonalDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteAsignacionPersonalService.modificarAsignacion(clienteUuid, asignacionUuid, username, clienteAsignacionPersonalDto);
    }

    @DeleteMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI + "/{asignacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteAsignacionPersonalDto eliminarAsignacion(
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "asignacionUuid") String asignacionUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaClienteAsignacionPersonalService.eliminarAsignacion(clienteUuid, asignacionUuid, username);
    }
}
