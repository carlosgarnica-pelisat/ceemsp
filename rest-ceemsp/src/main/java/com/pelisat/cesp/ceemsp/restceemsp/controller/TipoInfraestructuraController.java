package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.TipoInfraestructuraDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.TipoInfraestructuraService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TipoInfraestructuraController {
    private final TipoInfraestructuraService tipoInfraestructuraService;
    private final JwtUtils jwtUtils;
    private static final String TIPO_INFRAESTRUCTURA_URI = "/catalogos/tipos-infraestructura";

    @Autowired
    public TipoInfraestructuraController(TipoInfraestructuraService tipoInfraestructuraService, JwtUtils jwtUtils) {
        this.tipoInfraestructuraService = tipoInfraestructuraService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = TIPO_INFRAESTRUCTURA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TipoInfraestructuraDto> obtenerTiposInfraestructura() {
        return tipoInfraestructuraService.obtenerTiposInfraestructura();
    }

    @GetMapping(value = TIPO_INFRAESTRUCTURA_URI + "/{tipoInfraestructuraUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TipoInfraestructuraDto obtenerTipoInfraestructuraPorUuid(
            @PathVariable(value = "tipoInfraestructuraUuid") String tipoInfraestructuraUuid
    ) {
        return tipoInfraestructuraService.obtenerTipoInfraestructuraPorUuid(tipoInfraestructuraUuid);
    }

    @PostMapping(value = TIPO_INFRAESTRUCTURA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TipoInfraestructuraDto guardarTipoInfraestructura(
            HttpServletRequest request,
            @RequestBody TipoInfraestructuraDto tipoInfraestructuraDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return tipoInfraestructuraService.guardarTipoInfraestructura(tipoInfraestructuraDto, username);
    }
}
