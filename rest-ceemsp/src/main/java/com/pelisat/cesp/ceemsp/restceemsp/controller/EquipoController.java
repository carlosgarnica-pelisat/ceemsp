package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EquipoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoMarcaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EquipoController {
    private final EquipoService equipoService;
    private final JwtUtils jwtUtils;
    private static final String EQUIPO_URI = "/catalogos/equipos";

    @Autowired
    public EquipoController(EquipoService equipoService, JwtUtils jwtUtils) {
        this.equipoService = equipoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EQUIPO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EquipoDto> obtenerEquipos() {
        return equipoService.obtenerEquipos();
    }

    @GetMapping(value = EQUIPO_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EquipoDto obtenerEquipoPorUuid(
            @PathVariable(value = "equipoUuid") String equipoUuid
    ) {
        return equipoService.obtenerEquipoPorUuid(equipoUuid);
    }

    @PostMapping(value = EQUIPO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EquipoDto guardarEquipo(
            HttpServletRequest request,
            @RequestBody EquipoDto equipoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return equipoService.guardarEquipo(equipoDto, username);
    }

    /*@PutMapping(value = VEHICULO_MARCA_URI + "/{vehiculoMarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoMarcaDto modificarArmaMarca(
            HttpServletRequest request,
            @RequestBody VehiculoMarcaDto VehiculoMarcaDto,
            @PathVariable(value = "vehiculoMarcaUuid") String vehiculoMarcaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoMarcaService.modificar(VehiculoMarcaDto, vehiculoMarcaUuid, username);
    }

    @DeleteMapping(value = VEHICULO_MARCA_URI + "/{vehiculoMarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoMarcaDto eliminarArmaMarca(
            @PathVariable(value = "vehiculoMarcaUuid") String vehiculoMarcaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoMarcaService.eliminar(vehiculoMarcaUuid, username);
    }*/
}
