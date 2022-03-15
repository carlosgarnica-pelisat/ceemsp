package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaPersonalCertificacionService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaPersonalCertificacionController {
    private final EmpresaPersonalCertificacionService personalCertificacionService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_URI = "/personas/{personaUuid}/certificaciones";

    @Autowired
    public EmpresaPersonalCertificacionController(EmpresaPersonalCertificacionService personalCertificacionService, JwtUtils jwtUtils) {
        this.personalCertificacionService = personalCertificacionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalCertificacionDto> obtenerPersonalCertificaciones(
            @PathVariable(name = "personaUuid") String personaUuid
    ) {
        return personalCertificacionService.obtenerCertificacionesPorPersona(personaUuid);
    }

    @PostMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalCertificacionDto guardarCertificacion(
            HttpServletRequest request,
            @RequestBody PersonalCertificacionDto personalCertificacionDto,
            @PathVariable(name = "personaUuid") String personaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalCertificacionService.guardarCertificacion(personaUuid, username, personalCertificacionDto);
    }

    @PutMapping(value = PERSONALIDAD_URI + "/{capacitacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalCertificacionDto modificarCertificacion(
            HttpServletRequest request,
            @RequestBody PersonalCertificacionDto personalCertificacionDto,
            @PathVariable(name = "personaUuid") String personaUuid,
            @PathVariable(name = "capacitacionUuid") String capacitacionUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalCertificacionService.modificarCertificacion(personaUuid, capacitacionUuid, username, personalCertificacionDto);
    }

    @DeleteMapping(value = PERSONALIDAD_URI + "/{capacitacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonalCertificacionDto eliminarCertificacion(
            @PathVariable(name = "personaUuid") String personaUuid,
            @PathVariable(name = "capacitacionUuid") String capacitacionUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalCertificacionService.eliminarCertificacion(personaUuid, capacitacionUuid, username);
    }
}
