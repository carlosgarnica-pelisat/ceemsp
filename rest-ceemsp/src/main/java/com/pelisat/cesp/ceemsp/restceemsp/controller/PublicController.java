package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ComunicadoGeneralDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;
import com.pelisat.cesp.ceemsp.database.dto.NextRegisterDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ComunicadoGeneralService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PublicController {
    private final PublicService publicService;
    private final ComunicadoGeneralService comunicadoGeneralService;

    @Autowired
    public PublicController(PublicService publicService, ComunicadoGeneralService comunicadoGeneralService) {
        this.publicService = publicService;
        this.comunicadoGeneralService = comunicadoGeneralService;
    }

    @PostMapping(value = "/public/register/next", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public NextRegisterDto generarSiguienteRegistro(
            @RequestBody NextRegisterDto nextRegisterDto
    ) {
        return publicService.findNextRegister(nextRegisterDto);
    }

    @PostMapping(value = "/public/vehiculos/existencias", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExisteVehiculoDto buscarExistenciaVehiculo(
            @RequestBody ExisteVehiculoDto request
    ) {
        return publicService.buscarExistenciaVehiculo(request);
    }

    @GetMapping(value = "/public/comunicados", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComunicadoGeneralDto> obtenerComunicados() {
        return comunicadoGeneralService.obtenerComunicadosGenerales();
    }

    @GetMapping(value = "/public/comunicados/ultimo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto obtenerUltimoComunicado() {
        return comunicadoGeneralService.obtenerUltimoComunicado();
    }
}
