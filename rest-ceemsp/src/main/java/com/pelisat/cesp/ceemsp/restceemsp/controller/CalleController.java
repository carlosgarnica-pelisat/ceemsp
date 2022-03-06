package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.CalleDto;
import com.pelisat.cesp.ceemsp.database.dto.EstadoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CalleService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EstadoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CalleController {
    private static final String CALLES_URI = "/catalogos/calles";
    private final CalleService calleService;

    @Autowired
    public CalleController(CalleService calleService) {
        this.calleService = calleService;
    }

    @GetMapping(value = CALLES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CalleDto> obtenerCalles(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        if(StringUtils.isBlank(query)) {
            return calleService.obtenerTodasLasCalles(limit);
        } else {
            return calleService.obtenerCallesPorQuery(query);
        }
    }
}
