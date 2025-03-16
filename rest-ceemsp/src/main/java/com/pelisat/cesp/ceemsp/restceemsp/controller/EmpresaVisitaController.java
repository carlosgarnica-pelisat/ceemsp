package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVisitaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaVisitaController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_VISITA_URI = "/empresas/{empresaUuid}/visitas";
    private final EmpresaVisitaService empresaVisitaService;

    @Autowired
    public EmpresaVisitaController(
            JwtUtils jwtUtils,
            EmpresaVisitaService empresaVisitaService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaVisitaService = empresaVisitaService;
    }

    @GetMapping(value = EMPRESA_VISITA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VisitaDto> obtenerVisitasPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaVisitaService.obtenerVisitasPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_VISITA_URI + "/{visitaUuid}")
    public VisitaDto obtenerVisitaPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "visitaUuid") String visitaUuid
    ) {
        return empresaVisitaService.obtenerVisitaPorUuid(empresaUuid, visitaUuid);
    }

    @GetMapping(value = EMPRESA_VISITA_URI + "/{visitaUuid}/archivos/{archivoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarArchivoVisita(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "visitaUuid") String visitaUuid,
            @PathVariable(value = "archivoUuid") String archivoUuid
    ) throws Exception {
        File file = empresaVisitaService.descargarArchivoVisita(empresaUuid, visitaUuid, archivoUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF); // TODO: Validar el tipo de imagen en funcion de su formato
        httpHeaders.setContentDispositionFormData("attachment",  file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }
}
