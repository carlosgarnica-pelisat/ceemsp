package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaTipoDto;
import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaTipoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.BuzonSalidaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BuzonSalidaController {
    private final JwtUtils jwtUtils;
    private final BuzonSalidaService buzonSalidaService;
    private static final String BUZON_SALIDA_URI = "/buzon-salida";

    @Autowired
    public BuzonSalidaController(JwtUtils jwtUtils, BuzonSalidaService buzonSalidaService) {
        this.jwtUtils = jwtUtils;
        this.buzonSalidaService = buzonSalidaService;
    }

    @GetMapping(value = BUZON_SALIDA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BuzonInternoDto> obtenerArmasMarcas() {
        return buzonSalidaService.obtenerTodosLosMensajes();
    }

    @GetMapping(value = BUZON_SALIDA_URI + "/{buzonInternoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDto obtenerArmaMarcaPorUuid(
            @PathVariable(value = "buzonInternoUuid") String buzonInternoUuid
    ) {
        return buzonSalidaService.obtenerBuzonInternoPorUuid(buzonInternoUuid);
    }

    @PostMapping(value = BUZON_SALIDA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDto guardarArmaMarca(
            HttpServletRequest request,
            @RequestBody BuzonInternoDto buzonInternoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return buzonSalidaService.guardarBuzonInterno(buzonInternoDto, username);
    }

    @PutMapping(value = BUZON_SALIDA_URI + "/{buzonInternoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDto modificarMensajeBuzonSalida(
            HttpServletRequest request,
            @RequestBody BuzonInternoDto buzonInternoDto,
            @PathVariable(value = "buzonInternoUuid") String buzonInternoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return buzonSalidaService.modificarBuzonInterno(buzonInternoUuid, buzonInternoDto, username);
    }

    @DeleteMapping(value = BUZON_SALIDA_URI + "/{buzonInternoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BuzonInternoDto eliminarMensajeBuzonSalida(
            HttpServletRequest request,
            @PathVariable(value = "buzonInternoUuid") String buzonInternoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return buzonSalidaService.eliminarBuzonInterno(buzonInternoUuid, username);
    }
}
