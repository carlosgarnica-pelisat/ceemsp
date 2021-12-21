package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;
import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanAdiestramientoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanCartillaVacunacionService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CanCartillaVacunacionController {
    private final CanCartillaVacunacionService canCartillaVacunacionService;
    private final JwtUtils jwtUtils;
    private static final String CAN_CARTILLA_URI = "/empresas/{empresaUuid}/canes/{canUuid}/cartillas";

    @Autowired
    public CanCartillaVacunacionController(CanCartillaVacunacionService canCartillaVacunacionService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.canCartillaVacunacionService = canCartillaVacunacionService;
    }

    @GetMapping(value = CAN_CARTILLA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanCartillaVacunacionDto> obtenerCanesCartillasPorCanUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canCartillaVacunacionService.obtenerCartillasVacunacionPorCanUuid(empresaUuid, canUuid);
    }

    @PostMapping(value = CAN_CARTILLA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanCartillaVacunacionDto guardarCanCartillaVacunacion(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @RequestBody CanCartillaVacunacionDto canCartillaVacunacionDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canCartillaVacunacionService.guardarCartillaVacunacion(empresaUuid, canUuid, username, canCartillaVacunacionDto);
    }
}
