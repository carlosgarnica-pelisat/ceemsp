package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.CanTipoAdiestramientoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanRazaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanTipoAdiestramientoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CanTipoAdiestramientoController {
    private final CanTipoAdiestramientoService canTipoAdiestramientoService;
    private final JwtUtils jwtUtils;
    private static final String CAN_TIPO_ADIESTRAMIENTO_URI = "/catalogos/canes/adiestramientos";

    @Autowired
    public CanTipoAdiestramientoController(CanTipoAdiestramientoService canTipoAdiestramientoService, JwtUtils jwtUtils) {
        this.canTipoAdiestramientoService = canTipoAdiestramientoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CAN_TIPO_ADIESTRAMIENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanTipoAdiestramientoDto> obtenerCanRazas() {
        return canTipoAdiestramientoService.obtenerTodos();
    }

    @GetMapping(value = CAN_TIPO_ADIESTRAMIENTO_URI + "/{canRazaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanTipoAdiestramientoDto obtenerCanRazaPorUuid(
            @PathVariable(value = "canRazaUuid") String canRazaUuid
    ) {
        return canTipoAdiestramientoService.obtenerPorUuid(canRazaUuid);
    }

    @PostMapping(value = CAN_TIPO_ADIESTRAMIENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanTipoAdiestramientoDto guardarCanRaza(
            HttpServletRequest request,
            @RequestBody CanTipoAdiestramientoDto CanTipoAdiestramientoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canTipoAdiestramientoService.crearNuevo(CanTipoAdiestramientoDto, username);
    }

    @PutMapping(value = CAN_TIPO_ADIESTRAMIENTO_URI + "/{canRazaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanTipoAdiestramientoDto modificarCanRaza(
            HttpServletRequest request,
            @RequestBody CanTipoAdiestramientoDto CanTipoAdiestramientoDto,
            @PathVariable(value = "canRazaUuid") String canRazaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canTipoAdiestramientoService.modificar(CanTipoAdiestramientoDto, canRazaUuid, username);
    }

    @DeleteMapping(value = CAN_TIPO_ADIESTRAMIENTO_URI + "/{canRazaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanTipoAdiestramientoDto eliminarCanRaza(
            @PathVariable(value = "canRazaUuid") String canRazaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canTipoAdiestramientoService.eliminar(canRazaUuid, username);
    }
}
