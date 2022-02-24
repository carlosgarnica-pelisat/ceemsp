package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoColorDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoColorService;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoMarcaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VehiculoColorController {
    private final VehiculoColorService vehiculoColorService;
    private final JwtUtils jwtUtils;
    private static final String VEHICULO_COLOR_URI = "/empresas/{empresaUuid}/vehiculos/{vehiculoUuid}/colores";

    @Autowired
    public VehiculoColorController(VehiculoColorService vehiculoColorService, JwtUtils jwtUtils) {
        this.vehiculoColorService = vehiculoColorService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VEHICULO_COLOR_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoColorDto> obtenerColorVehiculos(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid
    ) {
        return vehiculoColorService.obtenerTodosPorVehiculoUuid(vehiculoUuid, empresaUuid);
    }

    @PostMapping(value = VEHICULO_COLOR_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoColorDto guardarColorVehiculo(
            @PathVariable(value = "empr esaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            HttpServletRequest request,
            @RequestBody VehiculoColorDto vehiculoColorDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoColorService.guardarcolor(empresaUuid, vehiculoUuid, username, vehiculoColorDto);
    }

    @PutMapping(value = VEHICULO_COLOR_URI + "/{colorUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoColorDto modificarColorVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @PathVariable(value = "colorUuid") String colorUuid,
            HttpServletRequest request,
            @RequestBody VehiculoColorDto vehiculoColorDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoColorService.modificarColor(empresaUuid, vehiculoUuid, colorUuid, username, vehiculoColorDto);
    }

    @DeleteMapping(value = VEHICULO_COLOR_URI + "/{colorUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoColorDto eliminarColorVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @PathVariable(value = "colorUuid") String colorUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoColorService.eliminarColor(empresaUuid, vehiculoUuid, colorUuid, username);
    }
}
