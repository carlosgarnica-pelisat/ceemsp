package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ColoniaDto;
import com.pelisat.cesp.ceemsp.database.dto.EstadoDto;
import com.pelisat.cesp.ceemsp.database.dto.LocalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.MunicipioDto;
import com.pelisat.cesp.ceemsp.database.model.Colonia;
import com.pelisat.cesp.ceemsp.database.model.Localidad;
import com.pelisat.cesp.ceemsp.restceemsp.service.EstadoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EstadoController {
    private static final String ESTADO_URI = "/catalogos/estados";
    private final EstadoService estadoService;

    @Autowired
    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping(value = ESTADO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EstadoDto> obtenerEstados() {
        return estadoService.obtenerTodosLosEstados();
    }

    @GetMapping(value = ESTADO_URI + "/{estadoUuid}/municipios")
    public List<MunicipioDto> obtenerMunicipiosPorEstadoUuid(
            @PathVariable(value = "estadoUuid") String estadoUuid
    ) {
        return estadoService.obtenerMunicipiosPorEstadoUuid(estadoUuid);
    }

    @GetMapping(value = ESTADO_URI + "/{estadoUuid}/municipios/{municipioUuid}/localidades")
    public List<LocalidadDto> obtenerLocalidadesPorMunicipioYEstadoUuid(
            @PathVariable(value = "estadoUuid") String estadoUuid,
            @PathVariable(value = "municipioUuid") String municipioUuid
    ) {
        return estadoService.obtenerLocalidadesPorEstadoUuidYMunicipioUuid(estadoUuid, municipioUuid);
    }

    @GetMapping(value = ESTADO_URI + "/{estadoUuid}/municipios/{municipioUuid}/colonias")
    public List<ColoniaDto> obtenerColoniasPorMunicipioYEstadoUuid(
            @PathVariable(value = "estadoUuid") String estadoUuid,
            @PathVariable(value = "municipioUuid") String municipioUuid
    ) {
        return estadoService.obtenerColoniasPorEstadoUuidYMunicipioUuid(estadoUuid, municipioUuid);
    }
}
