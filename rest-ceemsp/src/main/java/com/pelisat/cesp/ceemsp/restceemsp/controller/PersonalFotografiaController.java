package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalFotografiaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class PersonalFotografiaController {
    private final PersonalFotografiaService personalFotografiaService;
    private final JwtUtils jwtUtils;
    private static final String PERSONAL_FOTOGRAFIAS_URI = "/empresas/{empresaUuid}/personas/{personaUuid}/fotografias";

    @Autowired
    public PersonalFotografiaController(PersonalFotografiaService personalFotografiaService, JwtUtils jwtUtils) {
        this.personalFotografiaService = personalFotografiaService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = PERSONAL_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void guardarFotografiaElemento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "personaUuid") String personaUuid,
            HttpServletRequest request,
            @RequestParam("fotografia") MultipartFile fotografia,
            @RequestParam("metadataArchivo") String metadataArchivo
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        personalFotografiaService.guardarPersonalFotografia(
                empresaUuid,
                personaUuid,
                username,
                fotografia,
                new Gson().fromJson(metadataArchivo, PersonalFotografiaMetadata.class)
        );
    }
}
