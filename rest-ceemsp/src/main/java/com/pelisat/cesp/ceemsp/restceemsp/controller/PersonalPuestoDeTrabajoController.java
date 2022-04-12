package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.PersonalPuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalPuestoDeTrabajoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PersonalPuestoDeTrabajoController {
    private final PersonalPuestoDeTrabajoService personalPuestoDeTrabajoService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_PUESTO_TRABAJO_URI = "/catalogos/personal/puestos";

    @Autowired
    public PersonalPuestoDeTrabajoController(PersonalPuestoDeTrabajoService personalPuestoDeTrabajoService, JwtUtils jwtUtils) {
        this.personalPuestoDeTrabajoService = personalPuestoDeTrabajoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONALIDAD_PUESTO_TRABAJO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalPuestoDeTrabajoDto> obtenerPuestosDeTrabajo(
    ) {
        return personalPuestoDeTrabajoService.obtenerTodos();
    }

    @GetMapping(value = PERSONALIDAD_PUESTO_TRABAJO_URI + "/{puestoTrabajoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonalPuestoDeTrabajoDto obtenerPuestoTrabajoPorUuid(
            @PathVariable(value = "puestoTrabajoUuid") String puestoTrabajoUuid
    ) {
        return personalPuestoDeTrabajoService.obtenerPorUuid(puestoTrabajoUuid);
    }

    @PostMapping(value = PERSONALIDAD_PUESTO_TRABAJO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalPuestoDeTrabajoDto guardarPuestoTrabajo(
            HttpServletRequest request,
            @RequestBody PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalPuestoDeTrabajoService.crearNuevo(personalPuestoDeTrabajoDto, username);
    }

    @PutMapping(value = PERSONALIDAD_PUESTO_TRABAJO_URI  + "/{puestoTrabajoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalPuestoDeTrabajoDto modificarPuestoTrabajo(
            HttpServletRequest request,
            @RequestBody PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto,
            @PathVariable(value = "puestoTrabajoUuid") String puestoTrabajoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalPuestoDeTrabajoService.modificar(puestoTrabajoUuid, username, personalPuestoDeTrabajoDto);
    }

    @DeleteMapping(value = PERSONALIDAD_PUESTO_TRABAJO_URI  + "/{puestoTrabajoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonalPuestoDeTrabajoDto eliminarPuestoTrabajo(
            HttpServletRequest request,
            @PathVariable(value = "puestoTrabajoUuid") String puestoTrabajoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalPuestoDeTrabajoService.eliminar(puestoTrabajoUuid, username);
    }
}
