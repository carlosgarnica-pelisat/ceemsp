package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteAsignacionPersonalDto;
import com.pelisat.cesp.ceemsp.database.model.ClienteAsignacionPersonal;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteAsignacionPersonalService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClienteAsignacionPersonalController {
    private final ClienteAsignacionPersonalService clienteAsignacionPersonalService;
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_ASIGNACION_PERSONAL_URI =  "/empresas/{empresaUuid}/clientes/{clienteUuid}/asignaciones";

    @Autowired
    public ClienteAsignacionPersonalController(ClienteAsignacionPersonalService clienteAsignacionPersonalService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.clienteAsignacionPersonalService = clienteAsignacionPersonalService;
    }

    @GetMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteAsignacionPersonalDto> obtenerAsignacionesPorCliente(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) {
        return clienteAsignacionPersonalService.obtenerAsignacionesCliente(empresaUuid, clienteUuid);
    }

    @GetMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI + "/todas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteAsignacionPersonalDto> obtenerAsignacionesPorClienteTodas(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) {
        return clienteAsignacionPersonalService.obtenerAsignacionesClienteTodo(empresaUuid, clienteUuid);
    }

    @PostMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteAsignacionPersonalDto guardarAsignacion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @RequestBody ClienteAsignacionPersonalDto clienteAsignacionPersonalDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteAsignacionPersonalService.crearAsignacion(empresaUuid, clienteUuid, username, clienteAsignacionPersonalDto);
    }

    @PutMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI + "/{asignacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteAsignacionPersonalDto modificarAsignacion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "asignacionUuid") String asignacionUuid,
            @RequestBody ClienteAsignacionPersonalDto clienteAsignacionPersonalDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteAsignacionPersonalService.modificarAsignacion(empresaUuid, clienteUuid, asignacionUuid, username, clienteAsignacionPersonalDto);
    }

    @DeleteMapping(value = CLIENTE_ASIGNACION_PERSONAL_URI + "/{asignacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteAsignacionPersonalDto eliminarAsignacion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "asignacionUuid") String asignacionUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteAsignacionPersonalService.eliminarAsignacion(empresaUuid, clienteUuid, asignacionUuid, username);
    }
}
