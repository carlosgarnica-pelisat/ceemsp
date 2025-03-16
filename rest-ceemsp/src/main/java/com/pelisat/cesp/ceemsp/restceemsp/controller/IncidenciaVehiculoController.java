package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.IncidenciaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class IncidenciaVehiculoController {
    private final JwtUtils jwtUtils;
    private final IncidenciaVehiculoService incidenciaVehiculoService;
    private static final String INCIDENCIA_VEHICULO_URI = "/empresas/{empresaUuid}/incidencias/{incidenciaUuid}/vehiculos";

    @Autowired
    public IncidenciaVehiculoController(
            JwtUtils jwtUtils, IncidenciaVehiculoService incidenciaVehiculoService
    ) {
        this.jwtUtils = jwtUtils;
        this.incidenciaVehiculoService = incidenciaVehiculoService;
    }

    @PostMapping(value = INCIDENCIA_VEHICULO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto guardarIncidenciaVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody VehiculoDto vehiculoDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaVehiculoService.agregarVehiculoIncidencia(empresaUuid, incidenciaUuid, username, vehiculoDto);
    }

    @DeleteMapping(value = INCIDENCIA_VEHICULO_URI + "/{vehiculoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto eliminarIncidenciaVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return incidenciaVehiculoService.eliminarVehiculoIncidencia(empresaUuid, incidenciaUuid, vehiculoUuid, username);
    }
}
