package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.metadata.VisitaArchivoMetadata;
import com.pelisat.cesp.ceemsp.restceemsp.service.VisitaArchivoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VisitaArchivoController {
    private final VisitaArchivoService visitaArchivoService;
    private final JwtUtils jwtUtils;
    private static final String VISITA_ARCHIVO_URI = "/visitas/{visitaUuid}/archivos";

    @Autowired
    public VisitaArchivoController(VisitaArchivoService visitaArchivoService, JwtUtils jwtUtils) {
        this.visitaArchivoService = visitaArchivoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VISITA_ARCHIVO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VisitaArchivoMetadata> mostrarArchivosVisita(
            @PathVariable(value = "visitaUuid") String visitaUuid
    ) {
        return visitaArchivoService.obtenerArchivosPorVisita(visitaUuid);
    }

    @GetMapping(value = VISITA_ARCHIVO_URI + "/{archivoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarArchivoVisita(
            @PathVariable(value = "visitaUuid") String visitaUuid,
            @PathVariable(value = "archivoUuid") String archivoUuid
    ) throws Exception {
        File file = visitaArchivoService.descargarArchivoVisita(visitaUuid, archivoUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF); // TODO: Validar el tipo de imagen en funcion de su formato
        httpHeaders.setContentDispositionFormData("attachment",  file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = VISITA_ARCHIVO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void guardarArchivoVisita(
            @PathVariable(value = "visitaUuid") String visitaUuid,
            HttpServletRequest request,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("metadataArchivo") String metadataArchivo
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        visitaArchivoService.guardarArchivo(
                visitaUuid,
                username,
                archivo,
                new Gson().fromJson(metadataArchivo, VisitaArchivoMetadata.class)
        );
    }

    @DeleteMapping(value = VISITA_ARCHIVO_URI + "/{archivoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VisitaArchivoMetadata eliminarArchivoVisita(
            @PathVariable(value = "visitaUuid") String visitaUuid,
            @PathVariable(value = "archivoUuid") String archivoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return visitaArchivoService.eliminarArchivo(visitaUuid, archivoUuid, username);
    }
}
