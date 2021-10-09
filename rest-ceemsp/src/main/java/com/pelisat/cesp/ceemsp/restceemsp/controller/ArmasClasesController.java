package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ArmaClaseDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaClaseService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ArmasClasesController {
    private final ArmaClaseService armaClaseService;
    private final JwtUtils jwtUtils;
    private static final String ARMAS_CLASES_URI = "/catalogos/armas/clases";

    @Autowired
    public ArmasClasesController(ArmaClaseService armaClaseService, JwtUtils jwtUtils) {
        this.armaClaseService = armaClaseService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = ARMAS_CLASES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaClaseDto> obtenerArmasClases() {
        return armaClaseService.obtenerTodos();
    }

    @GetMapping(value = ARMAS_CLASES_URI + "/{armaClaseUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaClaseDto obtenerArmaClasePorUuid(
            @PathVariable(value = "armaClaseUuid") String armaClaseUuid
    ) {
        return armaClaseService.obtenerPorUuid(armaClaseUuid);
    }

    @PostMapping(value = ARMAS_CLASES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaClaseDto guardarArmaClase(
            HttpServletRequest request,
            @RequestBody ArmaClaseDto ArmaClaseDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaClaseService.crearNuevo(ArmaClaseDto, username);
    }

    @PutMapping(value = ARMAS_CLASES_URI + "/{armaClaseUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaClaseDto modificarArmaClase(
            HttpServletRequest request,
            @RequestBody ArmaClaseDto ArmaClaseDto,
            @PathVariable(value = "armaClaseUuid") String armaClaseUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaClaseService.modificar(ArmaClaseDto, armaClaseUuid, username);
    }

    @DeleteMapping(value = ARMAS_CLASES_URI + "/{armaClaseUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArmaClaseDto eliminarArmaClase(
            @PathVariable(value = "armaClaseUuid") String armaClaseUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaClaseService.eliminar(armaClaseUuid, username);
    }
}
