package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.metadata.IncidenciaArchivoMetadata;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaIncidenciaArchivoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/v1")
public class EmpresaIncidenciaArchivoController {
    private final JwtUtils jwtUtils;
    private final EmpresaIncidenciaArchivoService empresaIncidenciaArchivoService;
    private static final String INCIDENCIA_ARCHIVO_URI = "/incidencias/{incidenciaUuid}/archivos";

    @Autowired
    public EmpresaIncidenciaArchivoController(JwtUtils jwtUtils, EmpresaIncidenciaArchivoService empresaIncidenciaArchivoService) {
        this.jwtUtils = jwtUtils;
        this.empresaIncidenciaArchivoService = empresaIncidenciaArchivoService;
    }

    @GetMapping(path = INCIDENCIA_ARCHIVO_URI + "/{incidenciaArchivoUuid}/descargar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarIncidenciaArchivo(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "incidenciaArchivoUuid") String incidenciaArchivoUuid
    ) throws Exception {
        File file = empresaIncidenciaArchivoService.descargarArchivoIncidencia(incidenciaUuid, incidenciaArchivoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(path = INCIDENCIA_ARCHIVO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IncidenciaArchivoMetadata guardarIncidenciaArchivo(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @RequestParam("archivo") MultipartFile archivo,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaArchivoService.agregarArchivoIncidencia(incidenciaUuid, username, archivo);
    }

    @DeleteMapping(path = INCIDENCIA_ARCHIVO_URI + "/{incidenciaArchivoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IncidenciaArchivoMetadata eliminarIncidenciaArchivo(
            @PathVariable(value = "incidenciaUuid") String incidenciaUuid,
            @PathVariable(value = "incidenciaArchivoUuid") String incidenciaArchivoUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaIncidenciaArchivoService.eliminarArchivoIncidencia(incidenciaUuid, incidenciaArchivoUuid, username);
    }
}
