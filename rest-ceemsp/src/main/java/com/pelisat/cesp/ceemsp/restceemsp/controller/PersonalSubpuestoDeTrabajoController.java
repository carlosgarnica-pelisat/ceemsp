package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.PersonalPuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalSubpuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalPuestoDeTrabajoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalSubpuestoDeTrabajoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalSubpuestoDeTrabajoServiceImpl;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PersonalSubpuestoDeTrabajoController {
    private final PersonalSubpuestoDeTrabajoService personalSubpuestoDeTrabajoService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_SUBPUESTO_TRABAJO_URI = "/catalogos/personal/puestos/{puestoUuid}/subpuestos";

    @Autowired
    public PersonalSubpuestoDeTrabajoController(PersonalSubpuestoDeTrabajoService personalSubpuestoDeTrabajoService, JwtUtils jwtUtils) {
        this.personalSubpuestoDeTrabajoService = personalSubpuestoDeTrabajoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONALIDAD_SUBPUESTO_TRABAJO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalSubpuestoDeTrabajoDto> obtenerSubpuestosDeTrabajo(
            @PathVariable(value = "puestoUuid") String puestoUuid
    ) {
        return personalSubpuestoDeTrabajoService.obtenerTodos(puestoUuid);
    }

    @PostMapping(value = PERSONALIDAD_SUBPUESTO_TRABAJO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalSubpuestoDeTrabajoDto guardarSubpuestoTrabajo(
            HttpServletRequest request,
            @RequestBody PersonalSubpuestoDeTrabajoDto personalPuestoDeTrabajoDto,
            @PathVariable(value = "puestoUuid") String puestoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalSubpuestoDeTrabajoService.crearNuevo(personalPuestoDeTrabajoDto, username, puestoUuid);
    }
}
