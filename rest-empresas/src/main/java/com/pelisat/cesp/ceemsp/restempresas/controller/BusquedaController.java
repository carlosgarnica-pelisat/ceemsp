package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.RealizarBusquedaDto;
import com.pelisat.cesp.ceemsp.database.dto.ResultadosBusquedaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.BusquedaService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class BusquedaController {
    private final BusquedaService busquedaService;
    private final JwtUtils jwtUtils;
    private static final String BUSQUEDA_URI = "/busqueda";

    @Autowired
    public BusquedaController(BusquedaService busquedaService, JwtUtils jwtUtils) {
        this.busquedaService = busquedaService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = BUSQUEDA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultadosBusquedaDto realizarBusqueda(
            HttpServletRequest request,
            @RequestBody RealizarBusquedaDto realizarBusquedaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return busquedaService.realizarBusqueda(realizarBusquedaDto, username);
    }
}
