package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteAsignacionPersonalDto;
import com.pelisat.cesp.ceemsp.database.dto.ClienteModalidadDto;
import com.pelisat.cesp.ceemsp.database.model.ClienteModalidad;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteAsignacionPersonalService;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteModalidadService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClienteModalidadController {
    private final ClienteModalidadService clienteModalidadService;
    private final JwtUtils jwtUtils;
    private static final String CLIENTE_MODALIDADES_PERSONAL_URI =  "/empresas/{empresaUuid}/clientes/{clienteUuid}/modalidades";

    @Autowired
    public ClienteModalidadController(
            ClienteModalidadService clienteModalidadService, JwtUtils jwtUtils
    ) {
        this.clienteModalidadService = clienteModalidadService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteModalidadDto> obtenerAsignacionesPorCliente(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) {
        return clienteModalidadService.obtenerModalidadesPorCliente(empresaUuid, clienteUuid);
    }

    @PostMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteModalidadDto guardarAsignacion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @RequestBody ClienteModalidadDto clienteModalidadDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteModalidadService.guardarModalidadCliente(empresaUuid, clienteUuid, username, clienteModalidadDto);
    }

    @PutMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI + "/{modalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteModalidadDto modificarAsignacion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "modalidadUuid") String asignacionUuid,
            @RequestBody ClienteModalidadDto clienteModalidadDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteModalidadService.modificarModalidadCliente(empresaUuid, clienteUuid, asignacionUuid, username, clienteModalidadDto);
    }

    @DeleteMapping(value = CLIENTE_MODALIDADES_PERSONAL_URI + "/{modalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteModalidadDto eliminarAsignacion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @PathVariable(value = "modalidadUuid") String modalidadUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteModalidadService.eliminarModalidad(empresaUuid, clienteUuid, modalidadUuid, username);
    }
}
