package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaDomicilioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClienteController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_CLIENTES_URI = "/empresas/{empresaUuid}/clientes";
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(JwtUtils jwtUtils, ClienteService clienteService) {
        this.clienteService = clienteService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_CLIENTES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerClientePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return clienteService.obtenerClientesPorEmpresa(empresaUuid);
    }

    @PostMapping(value = EMPRESA_CLIENTES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto guardarClienteEmpresa(
            @RequestBody ClienteDto clienteDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.crearCliente(empresaUuid, username, clienteDto);
    }
}
