package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.NextRegisterDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ComunicadoGeneralService;
import com.pelisat.cesp.ceemsp.restceemsp.service.PublicService;
import com.pelisat.cesp.ceemsp.restceemsp.service.ReporteoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/v1")
public class ReporteoController {
    private final ReporteoService reporteoService;

    @Autowired
    public ReporteoController(ReporteoService reporteoService) {
        this.reporteoService = reporteoService;
    }

    @PostMapping(value = "/reporteo/listado-nominal", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteListadoNominal(
    ) throws Exception {
        File resultado = reporteoService.generarReporteListadoNominal();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/padron", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReportePadron(
    ) throws Exception {
        File resultado = reporteoService.generarReportePadronEmpresas();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }
}
