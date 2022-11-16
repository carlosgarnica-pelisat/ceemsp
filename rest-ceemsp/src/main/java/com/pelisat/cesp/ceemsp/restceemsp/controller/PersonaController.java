package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalNacionalidadDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalNacionalidadService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PersonaController {
    private final PersonaService personaService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_URI = "/empresas/{empresaUuid}/personas";

    @Autowired
    public PersonaController(PersonaService personaService, JwtUtils jwtUtils) {
        this.personaService = personaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerNacionalidades(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return personaService.obtenerTodos(empresaUuid);
    }

    @GetMapping(value = PERSONALIDAD_URI + "/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonaDto> obtenerPersonasEliminados(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return personaService.obtenerPersonasEliminadas(empresaUuid);
    }

    @GetMapping(value = PERSONALIDAD_URI + "/{personaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto obtenerPersonaPorUuid(
            @PathVariable(value = "personaUuid") String personaUuid,
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return personaService.obtenerPorUuid(empresaUuid, personaUuid);
    }

    @PostMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto guardarPersona(
            HttpServletRequest request,
            @RequestBody PersonaDto personalDto,
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.crearNuevo(personalDto, username, empresaUuid);
    }

    @PutMapping(value = PERSONALIDAD_URI + "/{personaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonaDto modificarPersona(
            HttpServletRequest request,
            @RequestBody PersonaDto personaDto,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "personaUuid") String personaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.modificarPersona(empresaUuid, personaUuid, username, personaDto);
    }

    @PutMapping(value = PERSONALIDAD_URI + "/{personaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PersonaDto eliminarPersona(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "personaUuid") String personaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("persona") String persona
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.eliminarPersona(empresaUuid, personaUuid, username, new Gson().fromJson(persona, PersonaDto.class), archivo);
    }

    @PutMapping(value = PERSONALIDAD_URI + "/{personaUuid}/puestos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PersonaDto modificarPuestoTrabajo(
            HttpServletRequest request,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("persona") String persona,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "personaUuid") String personaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personaService.modificarInformacionPuesto(new Gson().fromJson(persona, PersonaDto.class), username, empresaUuid, personaUuid, archivo);
    }
}
