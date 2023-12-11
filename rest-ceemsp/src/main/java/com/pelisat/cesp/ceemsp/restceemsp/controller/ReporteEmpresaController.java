package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.restceemsp.service.ReporteEmpresaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/v1")
public class ReporteEmpresaController {
    private final JwtUtils jwtUtils;
    private final ReporteEmpresaService reporteEmpresaService;

    @Autowired
    public ReporteEmpresaController(JwtUtils jwtUtils, ReporteEmpresaService reporteEmpresaService) {
        this.jwtUtils = jwtUtils;
        this.reporteEmpresaService = reporteEmpresaService;
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/acuerdos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteAcuerdos(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteAcuerdos(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/personal", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReportePersonal(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReportePersonal(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/escrituras", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteEscrituras(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteEscrituras(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/canes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteCanes(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteCanes(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/vehiculos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteVehiculos(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteVehiculos(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/clientes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteClientes(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteClientes(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/armas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteArmas(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteArmas(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/licencias-colectivas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteLicenciasColectivas(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteLicenciasColectivas(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/empresas/{empresaUuid}/reporteo/visitas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> generarReporteVisitas(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        File resultado = reporteEmpresaService.generarReporteVisitas(empresaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",  resultado.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(resultado));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }
}
