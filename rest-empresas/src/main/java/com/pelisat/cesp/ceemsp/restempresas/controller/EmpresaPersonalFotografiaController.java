package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaPersonalFotografiasService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaPersonalFotografiaController {
    private final EmpresaPersonalFotografiasService empresaPersonalFotografiasService;
    private final JwtUtils jwtUtils;
    private static final String PERSONAL_FOTOGRAFIAS_URI = "/personas/{personaUuid}/fotografias";

    @Autowired
    public EmpresaPersonalFotografiaController(EmpresaPersonalFotografiasService personalFotografiaService, JwtUtils jwtUtils) {
        this.empresaPersonalFotografiasService = personalFotografiaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = PERSONAL_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalFotografiaMetadata> listarFotografiasPorPersonal(
            @PathVariable(value = "personaUuid") String personaUuid
    ) {
        return empresaPersonalFotografiasService.mostrarPersonalFotografias(personaUuid);
    }

    @GetMapping(value = PERSONAL_FOTOGRAFIAS_URI + "/{fotografiaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarFotografiaPersonal(
            @PathVariable(value = "personaUuid") String personaUuid,
            @PathVariable(value = "fotografiaUuid") String fotografiaUuid
    ) throws Exception {
        File file = empresaPersonalFotografiasService.descargarFotografiaPersona(personaUuid, fotografiaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG); // TODO: Validar el tipo de imagen en funcion de su formato
        httpHeaders.setContentDispositionFormData("attachment",  file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = PERSONAL_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void guardarFotografiaElemento(
            @PathVariable(value = "personaUuid") String personaUuid,
            HttpServletRequest request,
            @RequestParam("fotografia") MultipartFile fotografia,
            @RequestParam("metadataArchivo") String metadataArchivo
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        empresaPersonalFotografiasService.guardarPersonalFotografia(
                personaUuid,
                username,
                fotografia,
                new Gson().fromJson(metadataArchivo, PersonalFotografiaMetadata.class)
        );
    }

    @DeleteMapping(value = PERSONAL_FOTOGRAFIAS_URI + "/{fotografiaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonalFotografiaMetadata eliminarFotografiaElemento(
            @PathVariable(value = "personaUuid") String personaUuid,
            @PathVariable(value = "fotografiaUuid") String fotografiaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaPersonalFotografiasService.eliminarPersonalFotografia(personaUuid, fotografiaUuid, username);
    }
}
