package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaTipoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaTipoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ArmaTipoController {
    private final ArmaTipoService armaTipoService;
    private final JwtUtils jwtUtils;
    private static final String ARMAS_TIPOS_URI = "/catalogos/armas/tipos";

    @Autowired
    public ArmaTipoController(ArmaTipoService armaTipoService, JwtUtils jwtUtils) {
        this.armaTipoService = armaTipoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = ARMAS_TIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaTipoDto> obtenerArmasMarcas() {
        return armaTipoService.obtenerTodos();
    }

    @GetMapping(value = ARMAS_TIPOS_URI + "/{armaTipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaTipoDto obtenerArmaMarcaPorUuid(
            @PathVariable(value = "armaTipoUuid") String armaTipoUuid
    ) {
        return armaTipoService.obtenerPorUuid(armaTipoUuid);
    }

    @PostMapping(value = ARMAS_TIPOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaTipoDto guardarArmaMarca(
            HttpServletRequest request,
            @RequestBody ArmaTipoDto ArmaTipoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaTipoService.crearNuevo(ArmaTipoDto, username);
    }

    @PutMapping(value = ARMAS_TIPOS_URI + "/{armaTipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaTipoDto modificarArmaMarca(
            HttpServletRequest request,
            @RequestBody ArmaTipoDto ArmaTipoDto,
            @PathVariable(value = "armaTipoUuid") String armaTipoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaTipoService.modificar(ArmaTipoDto, armaTipoUuid, username);
    }

    @DeleteMapping(value = ARMAS_TIPOS_URI + "/{armaTipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaTipoDto eliminarArmaMarca(
            @PathVariable(value = "armaTipoUuid") String armaTipoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaTipoService.eliminar(armaTipoUuid, username);
    }
}
