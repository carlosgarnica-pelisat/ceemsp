package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaIncidenciaVehiculoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class EmpresaIncidenciaVehiculoController {
    private final JwtUtils jwtUtils;
    private final EmpresaIncidenciaVehiculoService empresaIncidenciaVehiculoService;
    private static final String INCIDENCIA_VEHICULO_URI = "/incidencias/{incidenciaUuid}/vehiculos";

    @Autowired
    public EmpresaIncidenciaVehiculoController(
            JwtUtils jwtUtils, EmpresaIncidenciaVehiculoService empresaIncidenciaVehiculoService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaIncidenciaVehiculoService = empresaIncidenciaVehiculoService;
    }

    @PostMapping(value = INCIDENCIA_VEHICULO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto guardarIncidenciaVehiculo(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestBody VehiculoDto vehiculoDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaVehiculoService.agregarVehiculoIncidencia(incidenciaUuid, username, vehiculoDto);
    }

    @DeleteMapping(value = INCIDENCIA_VEHICULO_URI + "/{vehiculoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto eliminarIncidenciaVehiculo(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaVehiculoService.eliminarVehiculoIncidencia(incidenciaUuid, vehiculoUuid, username);
    }
}
