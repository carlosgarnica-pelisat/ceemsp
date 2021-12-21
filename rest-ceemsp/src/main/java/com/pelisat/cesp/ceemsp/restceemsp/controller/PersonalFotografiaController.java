package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.restceemsp.service.PersonaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class PersonalFotografiaController {
    private final PersonaService personaService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_FOTOGRAFIAS_URI = "/empresas/{empresaUuid}/personas/{personaUuid}/fotografias";

    @Autowired
    public PersonalFotografiaController(PersonaService personaService, JwtUtils jwtUtils) {
        this.personaService = personaService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = PERSONALIDAD_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void guardarFotografiaElemento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "personaUuid") String personaUuid,
            HttpServletRequest request,
            @RequestParam("fotografia") MultipartFile fotografia
            // TODO: Agregar metadata
    ) {

    }
}
