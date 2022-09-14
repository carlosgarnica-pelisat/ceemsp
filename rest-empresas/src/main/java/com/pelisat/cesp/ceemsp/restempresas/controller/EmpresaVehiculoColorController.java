package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoColorDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaVehiculoColorService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaVehiculoColorController {
    private final EmpresaVehiculoColorService vehiculoColorService;
    private final JwtUtils jwtUtils;
    private static final String VEHICULO_COLOR_URI = "/vehiculos/{vehiculoUuid}/colores";

    @Autowired
    public EmpresaVehiculoColorController(EmpresaVehiculoColorService vehiculoColorService, JwtUtils jwtUtils) {
        this.vehiculoColorService = vehiculoColorService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VEHICULO_COLOR_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoColorDto> obtenerColorVehiculos(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid
    ) {
        return vehiculoColorService.obtenerTodosPorVehiculoUuid(vehiculoUuid);
    }

    @PostMapping(value = VEHICULO_COLOR_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoColorDto guardarColorVehiculo(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            HttpServletRequest request,
            @RequestBody VehiculoColorDto vehiculoColorDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoColorService.guardarcolor(vehiculoUuid, username, vehiculoColorDto);
    }

    @PutMapping(value = VEHICULO_COLOR_URI + "/{colorUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoColorDto modificarColorVehiculo(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @PathVariable(value = "colorUuid") String colorUuid,
            HttpServletRequest request,
            @RequestBody VehiculoColorDto vehiculoColorDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoColorService.modificarColor(vehiculoUuid, colorUuid, username, vehiculoColorDto);
    }

    @DeleteMapping(value = VEHICULO_COLOR_URI + "/{colorUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoColorDto eliminarColorVehiculo(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @PathVariable(value = "colorUuid") String colorUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoColorService.eliminarColor(vehiculoUuid, colorUuid, username);
    }
}
