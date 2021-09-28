package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ModalidadService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CatalogoModalidadController {
    private final ModalidadService modalidadService;
    private final JwtUtils jwtUtils;
    private static final String MODALIDAD_URI = "/catalogos/modalidades";

    @Autowired
    public CatalogoModalidadController(ModalidadService modalidadService, JwtUtils jwtUtils) {
        this.modalidadService = modalidadService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = MODALIDAD_URI)
    public List<ModalidadDto> obtenerModalidades() {
        return modalidadService.obtenerModalidades();
    }

    @PostMapping(value = MODALIDAD_URI)
    public ModalidadDto crearModalidad(
            @RequestBody ModalidadDto modalidadDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return modalidadService.guardarModalidad(modalidadDto, username);
    }

    @GetMapping(value = MODALIDAD_URI + "/{modalidadUuid}")
    public ModalidadDto obtenerModalidadPorUuid(
            @PathVariable(value = "modalidadUuid") String modalidadUuid
    ) {
        return modalidadService.obtenerModalidadPorUuid(modalidadUuid);
    }

    @PutMapping(value = MODALIDAD_URI + "/{modalidadUuid}")
    public ModalidadDto modificarModalidad(
            @PathVariable(value = "modalidadUuid") String modalidadUuid,
            @RequestBody ModalidadDto modalidadDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return modalidadService.modificarModalidad(modalidadDto, modalidadUuid, username);
    }

    @DeleteMapping(value = MODALIDAD_URI + "/{modalidadUuid}")
    public ModalidadDto eliminarModalidad(
           @PathVariable (value = "modalidadUuid") String modalidadUuid,
           HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return modalidadService.eliminarModalidad(modalidadUuid, username);
    }
}
