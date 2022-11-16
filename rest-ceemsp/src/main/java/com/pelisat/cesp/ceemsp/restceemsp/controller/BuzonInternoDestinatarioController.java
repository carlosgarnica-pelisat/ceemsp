package com.pelisat.cesp.ceemsp.restceemsp.controller;


import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDestinatarioDto;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoDestinatarioRepository;
import com.pelisat.cesp.ceemsp.restceemsp.service.BuzonSalidaDestinatarioService;
import com.pelisat.cesp.ceemsp.restceemsp.service.BuzonSalidaDestinatarioServiceImpl;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class BuzonInternoDestinatarioController {
    private final JwtUtils jwtUtils;
    private final BuzonSalidaDestinatarioService buzonSalidaDestinatarioService;
    private static final String BUZON_SALIDA_URI = "/buzon-salida/{buzonUuid}/destinatarios";

    @Autowired
    public BuzonInternoDestinatarioController(JwtUtils jwtUtils, BuzonSalidaDestinatarioService buzonSalidaDestinatarioService) {
        this.jwtUtils = jwtUtils;
        this.buzonSalidaDestinatarioService = buzonSalidaDestinatarioService;
    }

    @PostMapping(value = BUZON_SALIDA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDestinatarioDto agregarDestinatario(
            @PathVariable(value = "buzonUuid") String buzonUuid,
            HttpServletRequest httpServletRequest,
            @RequestBody BuzonInternoDestinatarioDto requestBody
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return buzonSalidaDestinatarioService.agregarDestinatario(buzonUuid, username, requestBody);
    }

    @PutMapping(value = BUZON_SALIDA_URI + "/{destinatarioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDestinatarioDto modificarDestinatario(
            @PathVariable(value = "buzonUuid") String buzonUuid,
            @PathVariable(value = "destinatarioUuid") String destinatarioUuid,
            HttpServletRequest request,
            @RequestBody BuzonInternoDestinatarioDto buzonInternoDestinatarioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return buzonSalidaDestinatarioService.modificarDestinatario(buzonUuid, destinatarioUuid, username, buzonInternoDestinatarioDto);
    }

    @DeleteMapping(value = BUZON_SALIDA_URI + "/{destinatarioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDestinatarioDto borrarDestinatario(
            @PathVariable(value = "buzonUuid") String buzonUuid,
            @PathVariable(value = "destinatarioUuid") String destinatarioUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return buzonSalidaDestinatarioService.eliminarDestinatario(buzonUuid, destinatarioUuid, username);
    }
}
