package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoTipoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoTipoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VehiculoTipoController {
    private final VehiculoTipoService vehiculoTipoService;
    private final JwtUtils jwtUtils;
    private static final String VEHICULOS_TIPOS_URI = "/catalogos/vehiculos/tipos";

    @Autowired
    public VehiculoTipoController(VehiculoTipoService vehiculoTipoService, JwtUtils jwtUtils) {
        this.vehiculoTipoService = vehiculoTipoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VEHICULOS_TIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoTipoDto> obtenerVehiculosTipos() {
        return vehiculoTipoService.obtenerTodos();
    }

    @GetMapping(value = VEHICULOS_TIPOS_URI + "/{vehiculoTipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoTipoDto obtenerTipoVehiculoPorUuid(
            @PathVariable(value = "vehiculoTipoUuid") String vehiculoTipoUuid
    ) {
        return vehiculoTipoService.obtenerPorUuid(vehiculoTipoUuid);
    }

    @PostMapping(value = VEHICULOS_TIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoTipoDto guardarTipoVehiculo(
            HttpServletRequest request,
            @RequestBody VehiculoTipoDto VehiculoTipoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoTipoService.crearNuevo(VehiculoTipoDto, username);
    }

    @PutMapping(value = VEHICULOS_TIPOS_URI + "/{vehiculoTipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoTipoDto modificarArmaMarca(
            HttpServletRequest request,
            @RequestBody VehiculoTipoDto VehiculoTipoDto,
            @PathVariable(value = "vehiculoTipoUuid") String vehiculoTipoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoTipoService.modificar(VehiculoTipoDto, vehiculoTipoUuid, username);
    }

    @DeleteMapping(value = VEHICULOS_TIPOS_URI + "/{vehiculoTipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoTipoDto eliminarArmaMarca(
            @PathVariable(value = "vehiculoTipoUuid") String vehiculoTipoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoTipoService.eliminar(vehiculoTipoUuid, username);
    }
}
