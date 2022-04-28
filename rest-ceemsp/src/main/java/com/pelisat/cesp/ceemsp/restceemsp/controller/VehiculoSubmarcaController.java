package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoSubmarcaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoSubmarcaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class VehiculoSubmarcaController {
    private final VehiculoSubmarcaService vehiculoSubmarcaService;
    private final JwtUtils jwtUtils;
    private static final String VEHICULO_SUBMARCA_URI = "/catalogos/vehiculos/marcas/{marcaUuid}/submarcas";

    @Autowired
    public VehiculoSubmarcaController(VehiculoSubmarcaService vehiculoSubmarcaRepository, JwtUtils jwtUtils) {
        this.vehiculoSubmarcaService = vehiculoSubmarcaRepository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = VEHICULO_SUBMARCA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoSubmarcaDto guardarSubmarca(
            HttpServletRequest request,
            @RequestBody VehiculoSubmarcaDto vehiculoSubmarcaDto,
            @PathVariable(value = "marcaUuid") String marcaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoSubmarcaService.crearNuevo(vehiculoSubmarcaDto, marcaUuid, username);
    }

    @PutMapping(value = VEHICULO_SUBMARCA_URI + "/{vehiculoSubmarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoSubmarcaDto modificarSubmarca(
            HttpServletRequest request,
            @RequestBody VehiculoSubmarcaDto vehiculoSubmarcaDto,
            @PathVariable(value = "marcaUuid") String marcaUuid,
            @PathVariable(value = "vehiculoSubmarcaUuid") String vehiculoSubmarcaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoSubmarcaService.modificar(vehiculoSubmarcaDto, marcaUuid, vehiculoSubmarcaUuid, username);
    }

    @DeleteMapping(value = VEHICULO_SUBMARCA_URI + "/{vehiculoSubmarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoSubmarcaDto eliminarSubmarca(
            @PathVariable(value = "marcaUuid") String marcaUuid,
            @PathVariable(value = "vehiculoSubmarcaUuid") String vehiculoSubmarcaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoSubmarcaService.eliminar(marcaUuid, vehiculoSubmarcaUuid, username);
    }
}
