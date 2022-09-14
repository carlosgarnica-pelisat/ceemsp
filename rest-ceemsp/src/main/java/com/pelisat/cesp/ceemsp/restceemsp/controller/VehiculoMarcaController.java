package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;
import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoMarcaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VehiculoMarcaController {
    private final VehiculoMarcaService vehiculoMarcaService;
    private final JwtUtils jwtUtils;
    private static final String VEHICULO_MARCA_URI = "/catalogos/vehiculos/marcas";

    @Autowired
    public VehiculoMarcaController(VehiculoMarcaService vehiculoMarcaService, JwtUtils jwtUtils) {
        this.vehiculoMarcaService = vehiculoMarcaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VEHICULO_MARCA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoMarcaDto> obtenerArmasMarcas() {
        return vehiculoMarcaService.obtenerTodos();
    }

    @GetMapping(value = VEHICULO_MARCA_URI + "/tipos/{tipo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoMarcaDto> obtenerVehiculoMarcaTipo(
            @PathVariable(value = "tipo") VehiculoTipoEnum vehiculoTipoEnum
    ) {
        return vehiculoMarcaService.obtenerMarcaTipo(vehiculoTipoEnum);
    }

    @GetMapping(value = VEHICULO_MARCA_URI + "/{vehiculoMarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoMarcaDto obtenerArmaMarcaPorUuid(
            @PathVariable(value = "vehiculoMarcaUuid") String vehiculoMarcaUuid
    ) {
        return vehiculoMarcaService.obtenerPorUuid(vehiculoMarcaUuid);
    }

    @PostMapping(value = VEHICULO_MARCA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoMarcaDto guardarArmaMarca(
            HttpServletRequest request,
            @RequestBody VehiculoMarcaDto VehiculoMarcaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return vehiculoMarcaService.crearNuevo(VehiculoMarcaDto, username);
    }

    @PutMapping(value = VEHICULO_MARCA_URI + "/{vehiculoMarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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
    }
}
