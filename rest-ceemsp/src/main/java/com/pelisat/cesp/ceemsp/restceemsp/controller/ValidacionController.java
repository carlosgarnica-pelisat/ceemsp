package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.restceemsp.service.ValidacionService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class ValidacionController {
    private final ValidacionService validacionService;
    private static final String VALIDACION_URI = "/validaciones";

    @Autowired
    public ValidacionController(ValidacionService validacionService) {
        this.validacionService = validacionService;
    }

    @PostMapping(value = VALIDACION_URI + "/vehiculos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExisteVehiculoDto validarVehiculo(
            @RequestBody ExisteVehiculoDto existeVehiculoDto
    ) {
        return validacionService.buscarExistenciaVehiculo(existeVehiculoDto);
    }

    @PostMapping(value = VALIDACION_URI + "/personas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExistePersonaDto validarPersona(
            @RequestBody ExistePersonaDto existePersonaDto
    ) {
        return validacionService.buscarExistenciaPersona(existePersonaDto);
    }

    @PostMapping(value = VALIDACION_URI + "/empresas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExisteEmpresaDto validarEmpresa(
            @RequestBody ExisteEmpresaDto existeEmpresaDto
    ) {
        return validacionService.buscarExistenciaEmpresa(existeEmpresaDto);
    }

    @PostMapping(value = VALIDACION_URI + "/escrituras", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExisteEscrituraDto validarEscritura(
            @RequestBody ExisteEscrituraDto existeEscrituraDto
    ) {
        return validacionService.buscarEscrituraDto(existeEscrituraDto);
    }

    @PostMapping(value = VALIDACION_URI + "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExisteUsuarioDto buscarExistenciaUsuario(
            @RequestBody ExisteUsuarioDto existeUsuarioDto
    ) {
        return validacionService.buscarUsuario(existeUsuarioDto);
    }

    @PostMapping(value = VALIDACION_URI + "/armas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExisteArmaDto buscarExistenciaArma(
            @RequestBody ExisteArmaDto existeArmaDto
    ) {
        return validacionService.buscarArma(existeArmaDto);
    }
}
