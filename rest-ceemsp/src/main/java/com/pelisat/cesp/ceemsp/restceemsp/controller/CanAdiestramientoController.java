package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaTipoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanAdiestramientoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CanAdiestramientoController {
    private final CanAdiestramientoService canAdiestramientoService;
    private final JwtUtils jwtUtils;
    private static final String CAN_ADIESTRAMIENTO_URI = "/empresas/{empresaUuid}/canes/{canUuid}/adiestramientos";

    @Autowired
    public CanAdiestramientoController(CanAdiestramientoService canAdiestramientoService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.canAdiestramientoService = canAdiestramientoService;
    }

    @GetMapping(value = CAN_ADIESTRAMIENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanAdiestramientoDto> obtenerCanesAdiestramientoPorCanUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canAdiestramientoService.obtenerAdiestramientosPorCanUuid(empresaUuid, canUuid);
    }

    @PostMapping(value = CAN_ADIESTRAMIENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanAdiestramientoDto guardarCanAdiestramiento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @RequestBody CanAdiestramientoDto canAdiestramientoDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canAdiestramientoService.guardarCanAdiestramiento(empresaUuid, canUuid, username, canAdiestramientoDto);
    }
}
