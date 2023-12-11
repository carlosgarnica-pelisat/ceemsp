package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaClienteService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaClienteController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_CLIENTES_URI = "/clientes";
    private final EmpresaClienteService clienteService;

    @Autowired
    public EmpresaClienteController(JwtUtils jwtUtils, EmpresaClienteService clienteService) {
        this.clienteService = clienteService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_CLIENTES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDto> obtenerClientesPorUuidEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.obtenerClientesPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_CLIENTES_URI + "/{clienteUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto obtenerClientePorUuid(
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.obtenerClientePorUuid(username, clienteUuid, false);
    }

    @GetMapping(value = EMPRESA_CLIENTES_URI + "/{clienteUuid}/contrato", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarContratoPdf(
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        File file = clienteService.obtenerContrato(clienteUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = EMPRESA_CLIENTES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClienteDto guardarClienteEmpresa(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("cliente") String cliente,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.crearCliente(username, new Gson().fromJson(cliente, ClienteDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_CLIENTES_URI + "/{clienteUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDto modificarClienteEmpresa(
            HttpServletRequest request,
            @PathVariable(value = "clienteUuid") String clienteUuid,
            @RequestBody ClienteDto clienteDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.modificarCliente(clienteUuid, username, clienteDto);
    }

    @PutMapping(value = EMPRESA_CLIENTES_URI + "/{clienteUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClienteDto eliminarClienteEmpresa(
            HttpServletRequest request,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("cliente") String cliente,
            @PathVariable(value = "clienteUuid") String clienteUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return clienteService.eliminarCliente(clienteUuid, username, new Gson().fromJson(cliente, ClienteDto.class), archivo);
    }
}
