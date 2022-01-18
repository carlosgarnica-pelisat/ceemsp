package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.UniformeDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.UniformeService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UniformeController {
    private final UniformeService uniformeService;
    private final JwtUtils jwtUtils;
    private static final String UNIFORME_URI = "/catalogos/uniformes";

    @Autowired
    public UniformeController(UniformeService uniformeService, JwtUtils jwtUtils) {
        this.uniformeService = uniformeService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = UNIFORME_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UniformeDto> obtenerUniformes() {
        return uniformeService.obtenerUniformes();
    }

    @GetMapping(value = UNIFORME_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UniformeDto obtenerUniformePorUuid(
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return uniformeService.obtenerUniformePorUuid(uniformeUuid);
    }

    @PostMapping(value = UNIFORME_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UniformeDto guardarUniforme(
            HttpServletRequest request,
            @RequestBody UniformeDto uniformeDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return uniformeService.guardarUniforme(uniformeDto, username);
    }
}
