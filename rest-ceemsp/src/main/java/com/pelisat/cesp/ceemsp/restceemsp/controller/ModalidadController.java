package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ModalidadService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ModalidadController {
    private final ModalidadService modalidadService;
    private final JwtUtils jwtUtils;
    private static final String MODALIDAD_URI = "/catalogos/modalidades";

    @Autowired
    public ModalidadController(ModalidadService modalidadService, JwtUtils jwtUtils) {
        this.modalidadService = modalidadService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = MODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModalidadDto> obtenerModalidades(
            @RequestParam(name = "filterBy") String filterBy,
            @RequestParam(name = "filterValue") String filterValue
    ) {
        if(StringUtils.isBlank(filterBy)) {
            return modalidadService.obtenerModalidades();
        }
        return modalidadService.obtenerModalidadesFiltradoPor(
                StringEscapeUtils.escapeXml10(filterBy),
                StringEscapeUtils.escapeXml11(filterValue)
        );
    }

    @GetMapping(value = MODALIDAD_URI + "/{modalidadUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ModalidadDto obtenerModalidadPorUuid(
            @PathVariable(value = "modalidadUuid") String modalidadUuid
    ) {
        return modalidadService.obtenerModalidadPorUuid(modalidadUuid);
    }

    @PostMapping(value = MODALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModalidadDto guardarModalidad(
            HttpServletRequest request,
            @RequestBody ModalidadDto modalidadDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return modalidadService.guardarModalidad(modalidadDto, username);
    }

}
