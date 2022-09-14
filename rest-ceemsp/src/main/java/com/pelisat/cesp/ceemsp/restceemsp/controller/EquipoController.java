package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EquipoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoMarcaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EquipoController {
    private final EquipoService equipoService;
    private final JwtUtils jwtUtils;
    private static final String EQUIPO_URI = "/catalogos/equipos";

    @Autowired
    public EquipoController(EquipoService equipoService, JwtUtils jwtUtils) {
        this.equipoService = equipoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EQUIPO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EquipoDto> obtenerEquipos(
            @RequestParam(value = "empresaUuid", required = false) String empresaUuid
    ) {
        if(StringUtils.isNotBlank(empresaUuid)) {
            return equipoService.obtenerEquipos(empresaUuid);
        }
        return equipoService.obtenerEquipos();
    }

    @GetMapping(value = EQUIPO_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EquipoDto obtenerEquipoPorUuid(
            @PathVariable(value = "equipoUuid") String equipoUuid
    ) {
        return equipoService.obtenerEquipoPorUuid(equipoUuid);
    }

    @PostMapping(value = EQUIPO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EquipoDto guardarEquipo(
            HttpServletRequest request,
            @RequestBody EquipoDto equipoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return equipoService.guardarEquipo(equipoDto, username);
    }

    @PutMapping(value = EQUIPO_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EquipoDto modificarEquipo(
            HttpServletRequest request,
            @RequestBody EquipoDto equipoDto,
            @PathVariable(value = "equipoUuid") String equipoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return equipoService.modificarEquipo(equipoUuid, username, equipoDto);
    }

    @DeleteMapping(value = EQUIPO_URI + "/{equipoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EquipoDto eliminarEquipo(
            @PathVariable(value = "equipoUuid") String equipoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return equipoService.eliminarEquipo(equipoUuid, username);
    }
}
