package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.restceemsp.service.ComunicadoGeneralService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaService;
import com.pelisat.cesp.ceemsp.restceemsp.service.PublicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @PostMapping(value = "/public/visitas/siguiente", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProximaVisitaDto generarSiguienteVisita(
            @RequestBody ProximaVisitaDto proximaVisitaDto
    ) {
        return publicService.buscarProximaVisita(proximaVisitaDto);
    }

    @PostMapping(value = "/public/vehiculos/existencias", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ExisteVehiculoDto buscarExistenciaVehiculo(
            @RequestBody ExisteVehiculoDto request
    ) {
        return publicService.buscarExistenciaVehiculo(request);
    }

    @GetMapping(value = "/public/comunicados", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComunicadoGeneralDto> obtenerComunicados(
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "mes", required = false) String mes,
            @RequestParam(value = "ano", required = false) String ano
    ) {
        Integer mesInteger = null;
        Integer anoInteger = null;
        if(StringUtils.isNotBlank(mes) && !StringUtils.equals(mes, "null")) {
            mesInteger = Integer.parseInt(mes);
        }
        if(StringUtils.isNotBlank(ano) && !StringUtils.equals(ano, "null")) {
            anoInteger = Integer.parseInt(ano);
        }
        return comunicadoGeneralService.obtenerComunicadosGenerales(titulo, mesInteger, anoInteger);
    }

    @GetMapping(value = "/public/comunicados/ultimo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto obtenerUltimoComunicado() {
        return comunicadoGeneralService.obtenerUltimoComunicado();
    }

    @GetMapping(value = "/public/comunicados/{comunicadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto obtenerComunicadoPorUuid(
            @PathVariable(value = "comunicadoUuid") String comunicadoUuid
    ) {
        return comunicadoGeneralService.obtenerComunicadoPorUuid(comunicadoUuid);
    }

    @GetMapping(value = "/public/validar/acuse/{sello}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidarAcuseDto validarAcusePorSello(
            @PathVariable(value = "sello") String sello
    ) {
        return publicService.validarAcusePorSello(sello);
    }

    @GetMapping(value = "/public/validar/informe/{sello}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidarInformeDto validarInformePorSello(
            @PathVariable(value = "sello") String sello
    ) {
        return publicService.validarInformePorSello(sello);
    }

    @GetMapping(value = "/public/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("ping", "pong");
        return response;
    }
}
