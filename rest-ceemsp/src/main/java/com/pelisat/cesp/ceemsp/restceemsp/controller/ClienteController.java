package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ClienteService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaDomicilioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
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
    public List<ClienteDto> obtenerClientesPorUuidEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return clienteService.obtenerClientesPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_CLIENTES_URI + "/{clienteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto obtenerClientePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) {
        return clienteService.obtenerClientePorUuid(empresaUuid, clienteUuid, false);
    }

    @PostMapping(value = EMPRESA_CLIENTES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto guardarClienteEmpresa(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("escritura") String cliente,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.crearCliente(empresaUuid, username, new Gson().fromJson(cliente, ClienteDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_CLIENTES_URI + "/{clienteUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto modificarClienteEmpresa(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @RequestBody ClienteDto clienteDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.modificarCliente(empresaUuid, clienteUuid, username, clienteDto);
    }

    @DeleteMapping(value = EMPRESA_CLIENTES_URI + "/{clienteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto eliminarClienteEmpresa(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.eliminarCliente(empresaUuid, clienteUuid, username);
    }
}
