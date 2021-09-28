package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.CanRazaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanRazaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CanRazaController {
    private final CanRazaService catalogoCanRazaService;
    private final JwtUtils jwtUtils;
    private static final String CAN_RAZA_URI = "/catalogos/canes/razas";

    @Autowired
    public CanRazaController(CanRazaService catalogoCanRazaService, JwtUtils jwtUtils) {
        this.catalogoCanRazaService = catalogoCanRazaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CAN_RAZA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanRazaDto> obtenerCanRazas() {
        return catalogoCanRazaService.obtenerTodos();
    }

    @GetMapping(value = CAN_RAZA_URI + "/{canRazaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanRazaDto obtenerCanRazaPorUuid(
            @PathVariable(value = "canRazaUuid") String canRazaUuid
    ) {
        return catalogoCanRazaService.obtenerPorUuid(canRazaUuid);
    }

    @PostMapping(value = CAN_RAZA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanRazaDto guardarCanRaza(
            HttpServletRequest request,
            @RequestBody CanRazaDto canRazaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return catalogoCanRazaService.crearNuevo(canRazaDto, username);
    }

    @PutMapping(value = CAN_RAZA_URI + "/{canRazaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanRazaDto modificarCanRaza(
            HttpServletRequest request,
            @RequestBody CanRazaDto canRazaDto,
            @PathVariable(value = "canRazaUuid") String canRazaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return catalogoCanRazaService.modificar(canRazaDto, canRazaUuid, username);
    }

    @DeleteMapping(value = CAN_RAZA_URI + "/{canRazaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanRazaDto eliminarCanRaza(
            @PathVariable(value = "canRazaUuid") String canRazaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return catalogoCanRazaService.eliminar(canRazaUuid, username);
    }
}
