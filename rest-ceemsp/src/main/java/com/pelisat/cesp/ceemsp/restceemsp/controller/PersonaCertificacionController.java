package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.PersonalCertificacionService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PersonaCertificacionController {
    private final PersonalCertificacionService personalCertificacionService;
    private final JwtUtils jwtUtils;
    private static final String PERSONALIDAD_URI = "/empresas/{empresaUuid}/personas/{personaUuid}/certificaciones";

    @Autowired
    public PersonaCertificacionController(PersonalCertificacionService personalCertificacionService, JwtUtils jwtUtils) {
        this.personalCertificacionService = personalCertificacionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalCertificacionDto> obtenerPersonalCertificaciones(
            @PathVariable(name = "empresaUuid") String empresaUuid,
            @PathVariable(name = "personaUuid") String personaUuid
    ) {
        return personalCertificacionService.obtenerCertificacionesPorPersona(empresaUuid, personaUuid);
    }

    @GetMapping(value = PERSONALIDAD_URI + "/{capacitacionUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarCapacitacionPdf(
            @PathVariable(name = "empresaUuid") String empresaUuid,
            @PathVariable(name = "personaUuid") String personaUuid,
            @PathVariable(name = "capacitacionUuid") String capacitacionUuid
    ) throws Exception {
        File file = personalCertificacionService.obtenerPdfCertificacion(empresaUuid, personaUuid, capacitacionUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = PERSONALIDAD_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PersonalCertificacionDto guardarCertificacion(
            HttpServletRequest request,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("certificacion") String certificacion,
            @PathVariable(name = "empresaUuid") String empresaUuid,
            @PathVariable(name = "personaUuid") String personaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalCertificacionService.guardarCertificacion(empresaUuid, personaUuid, username, new Gson().fromJson(certificacion, PersonalCertificacionDto.class), archivo);
    }

    @PutMapping(value = PERSONALIDAD_URI + "/{capacitacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PersonalCertificacionDto modificarCertificacion(
            HttpServletRequest request,
            @PathVariable(name = "empresaUuid") String empresaUuid,
            @PathVariable(name = "personaUuid") String personaUuid,
            @PathVariable(name = "capacitacionUuid") String capacitacionUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(name = "certificacion") String certificacion
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalCertificacionService.modificarCertificacion(empresaUuid, personaUuid, capacitacionUuid, username, new Gson().fromJson(certificacion, PersonalCertificacionDto.class), archivo);
    }

    @DeleteMapping(value = PERSONALIDAD_URI + "/{capacitacionUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonalCertificacionDto eliminarCertificacion(
            @PathVariable(name = "empresaUuid") String empresaUuid,
            @PathVariable(name = "personaUuid") String personaUuid,
            @PathVariable(name = "capacitacionUuid") String capacitacionUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return personalCertificacionService.eliminarCertificacion(empresaUuid, personaUuid, capacitacionUuid, username);
    }
}
