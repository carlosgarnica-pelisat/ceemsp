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
        File resultado = reporteoService.generarReporteListadoNominal(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/padron", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReportePadron(
    ) throws Exception {
        File resultado = reporteoService.generarReportePadronEmpresas(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/intercambio-informacion", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteIntercambioInformacion(
    ) throws Exception {
        File resultado = reporteoService.generarReporteIntercambioInformacion(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/acuerdos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteAcuerdos() throws Exception {
        File resultado = reporteoService.generarReporteAcuerdos(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/personal", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReportePersonal() throws Exception {
        File resultado = reporteoService.generarReportePersonal(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/escrituras", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteEscrituras() throws Exception {
        File resultado = reporteoService.generarReporteEscrituras(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/canes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteCanes() throws Exception {
        File resultado = reporteoService.generarReporteCanes(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/vehiculos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteVehiculos() throws Exception {
        File resultado = reporteoService.generarReporteVehiculos(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/clientes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteClientes() throws Exception {
        File resultado = reporteoService.generarReporteClientes(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/armas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteArmas() throws Exception {
        File resultado = reporteoService.generarReporteArmas(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/licencias-colectivas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteLicenciasColectivas() throws Exception {
        File resultado = reporteoService.generarReporteLicenciasColectivas(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/reporteo/visitas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteVisitas() throws Exception {
        File resultado = reporteoService.generarReporteVisitas(null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }
}
