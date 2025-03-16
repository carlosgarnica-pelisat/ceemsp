package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.RealizarBusquedaDto;
import com.pelisat.cesp.ceemsp.database.dto.ResultadosBusquedaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.BusquedaService;
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
    private static final String BUSQUEDA_URI = "/busqueda";

    @Autowired
    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @PostMapping(value = BUSQUEDA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultadosBusquedaDto realizarBusqueda(
            HttpServletRequest request,
            @RequestBody RealizarBusquedaDto realizarBusquedaDto
    ) {
        return busquedaService.realizarBusqueda(realizarBusquedaDto);
    }
}
