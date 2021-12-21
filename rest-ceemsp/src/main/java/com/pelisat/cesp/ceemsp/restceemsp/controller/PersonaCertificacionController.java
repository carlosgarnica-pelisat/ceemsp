package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalCertificacionService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PersonaCertificacionController {
    private final PersonalCertificacionService personalCertificacionService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_URI = "/empresas/{empresaUuid}/personas/{personaUuid}/certificaciones";

    @Autowired
    public PersonaCertificacionController(PersonalCertificacionService personalCertificacionService, JwtUtils jwtUtils) {
        this.personalCertificacionService = personalCertificacionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalCertificacionDto> obtenerPersonalCertificaciones(
            @PathVariable(name = "empresaUuid") String empresaUuid,
            @PathVariable(name = "personaUuid") String personaUuid
    ) {
        return personalCertificacionService.obtenerCertificacionesPorPersona(empresaUuid, personaUuid);
    }

    @PostMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalCertificacionDto guardarCertificacion(
            HttpServletRequest request,
            @RequestBody PersonalCertificacionDto personalCertificacionDto,
            @PathVariable(name = "empresaUuid") String empresaUuid,
            @PathVariable(name = "personaUuid") String personaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalCertificacionService.guardarCertificacion(empresaUuid, personaUuid, username, personalCertificacionDto);
    }
}
