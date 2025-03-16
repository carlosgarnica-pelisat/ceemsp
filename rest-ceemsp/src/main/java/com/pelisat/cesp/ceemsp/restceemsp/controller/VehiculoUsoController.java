package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoUsoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoUsoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VehiculoUsoController {
    private final VehiculoUsoService vehiculoUsoService;
    private final JwtUtils jwtUtils;
    private static final String VEHICULO_USO_URI = "/catalogos/vehiculos/usos";

    @Autowired
    public VehiculoUsoController(VehiculoUsoService vehiculoUsoService, JwtUtils jwtUtils) {
        this.vehiculoUsoService = vehiculoUsoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VEHICULO_USO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoUsoDto> obtenerVehiculosUsos() {
        return vehiculoUsoService.obtenerTodos();
    }

    @GetMapping(value = VEHICULO_USO_URI + "/{vehiculoUsoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoUsoDto obtenerUsoVehiculoPorUuid(
            @PathVariable(value = "vehiculoUsoUuid") String vehiculoUsoUuid
    ) {
        return vehiculoUsoService.obtenerPorUuid(vehiculoUsoUuid);
    }

    @PostMapping(value = VEHICULO_USO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoUsoDto guardarUsoVehiculo(
            HttpServletRequest request,
            @RequestBody VehiculoUsoDto VehiculoUsoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoUsoService.crearNuevo(VehiculoUsoDto, username);
    }

    @PutMapping(value = VEHICULO_USO_URI + "/{vehiculoUsoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoUsoDto modificarUsoVehiculo(
            HttpServletRequest request,
            @RequestBody VehiculoUsoDto vehiculoUsoDto,
            @PathVariable(value = "vehiculoUsoUuid") String vehiculoUsoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoUsoService.modificar(vehiculoUsoDto, vehiculoUsoUuid, username);
    }

    @DeleteMapping(value = VEHICULO_USO_URI + "/{vehiculoUsoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoUsoDto eliminarUsoVehiculo(
            HttpServletRequest request,
            @PathVariable(value = "vehiculoUsoUuid") String vehiculoUsoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoUsoService.eliminar(vehiculoUsoUuid, username);
    }
}
