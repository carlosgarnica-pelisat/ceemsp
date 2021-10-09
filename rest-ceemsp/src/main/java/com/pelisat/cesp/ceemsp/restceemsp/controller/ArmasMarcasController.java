package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaMarcaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaMarcaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ArmasMarcasController {
    private final ArmaMarcaService armaMarcaService;
    private final JwtUtils jwtUtils;
    private static final String ARMAS_MARCAS_URI = "/catalogos/armas/marcas";

    @Autowired
    public ArmasMarcasController(ArmaMarcaService armaMarcaService, JwtUtils jwtUtils) {
        this.armaMarcaService = armaMarcaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = ARMAS_MARCAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaMarcaDto> obtenerArmasMarcas() {
        return armaMarcaService.obtenerTodos();
    }

    @GetMapping(value = ARMAS_MARCAS_URI + "/{armaMarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaMarcaDto obtenerArmaMarcaPorUuid(
            @PathVariable(value = "armaMarcaUuid") String armaMarcaUuid
    ) {
        return armaMarcaService.obtenerPorUuid(armaMarcaUuid);
    }

    @PostMapping(value = ARMAS_MARCAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaMarcaDto guardarArmaMarca(
            HttpServletRequest request,
            @RequestBody ArmaMarcaDto ArmaMarcaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaMarcaService.crearNuevo(ArmaMarcaDto, username);
    }

    @PutMapping(value = ARMAS_MARCAS_URI + "/{armaMarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaMarcaDto modificarArmaMarca(
            HttpServletRequest request,
            @RequestBody ArmaMarcaDto ArmaMarcaDto,
            @PathVariable(value = "armaMarcaUuid") String armaMarcaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaMarcaService.modificar(ArmaMarcaDto, armaMarcaUuid, username);
    }

    @DeleteMapping(value = ARMAS_MARCAS_URI + "/{armaMarcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaMarcaDto eliminarArmaMarca(
            @PathVariable(value = "armaMarcaUuid") String armaMarcaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaMarcaService.eliminar(armaMarcaUuid, username);
    }
}
