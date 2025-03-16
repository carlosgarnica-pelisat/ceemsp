package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalNacionalidadDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalNacionalidadService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PersonalNacionalidadController {
    private final PersonalNacionalidadService personalNacionalidadService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_NACIONALIDAD_URI = "/catalogos/personal/nacionalidades";

    @Autowired
    public PersonalNacionalidadController(PersonalNacionalidadService personalNacionalidadService, JwtUtils jwtUtils) {
        this.personalNacionalidadService = personalNacionalidadService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONALIDAD_NACIONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalNacionalidadDto> obtenerNacionalidades(
    ) {
        return personalNacionalidadService.obtenerTodos();
    }

    @GetMapping(value = PERSONALIDAD_NACIONALIDAD_URI + "/{nacionalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonalNacionalidadDto obtenerNacionalidadPorUuid(
            @PathVariable(value = "nacionalidadUuid") String nacionalidadUuid
    ) {
        return personalNacionalidadService.obtenerPorUuid(nacionalidadUuid);
    }

    @PostMapping(value = PERSONALIDAD_NACIONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalNacionalidadDto guardarNacionalidad(
            HttpServletRequest request,
            @RequestBody PersonalNacionalidadDto personalNacionalidadDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalNacionalidadService.crearNuevo(personalNacionalidadDto, username);
    }

    @PutMapping(value = PERSONALIDAD_NACIONALIDAD_URI + "/{nacionalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonalNacionalidadDto modificarNacionalidad(
            HttpServletRequest request,
            @RequestBody PersonalNacionalidadDto personalNacionalidadDto,
            @PathVariable(value = "nacionalidadUuid") String nacionalidadUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalNacionalidadService.modificarNacionalidad(nacionalidadUuid, username, personalNacionalidadDto);
    }

    @DeleteMapping(value = PERSONALIDAD_NACIONALIDAD_URI + "/{nacionalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonalNacionalidadDto eliminarNacionalidad(
            HttpServletRequest request,
            @PathVariable(value = "nacionalidadUuid") String nacionalidadUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalNacionalidadService.eliminarNacionalidad(nacionalidadUuid, username);
    }
}
