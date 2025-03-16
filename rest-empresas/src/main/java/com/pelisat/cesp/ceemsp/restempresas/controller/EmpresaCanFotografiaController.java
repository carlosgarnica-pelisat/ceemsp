package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.metadata.CanFotografiaMetadata;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaCanFotografiaService;
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
public class EmpresaCanFotografiaController {
    private final EmpresaCanFotografiaService empresaCanFotografiaService;
    private final JwtUtils jwtUtils;
    private static final String CAN_FOTOGRAFIAS_URI = "/canes/{canUuid}/fotografias";

    @Autowired
    public EmpresaCanFotografiaController(EmpresaCanFotografiaService empresaCanFotografiaService, JwtUtils jwtUtils) {
        this.empresaCanFotografiaService = empresaCanFotografiaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CAN_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanFotografiaMetadata> listarFotografiasPorCan(
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return empresaCanFotografiaService.mostrarCanFotografias(canUuid);
    }

    @GetMapping(value = CAN_FOTOGRAFIAS_URI + "/{fotografiaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarFotografiaCan(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "fotografiaUuid") String fotografiaUuid
    ) throws Exception {
        File file = empresaCanFotografiaService.descargarFotografiaCan(canUuid, fotografiaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG); // TODO: Validar el tipo de imagen en funcion de su formato
        httpHeaders.setContentDispositionFormData("attachment",  file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = CAN_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void guardarFotografiaCan(
            @PathVariable(value = "canUuid") String vehiculoUuid,
            HttpServletRequest request,
            @RequestParam("fotografia") MultipartFile fotografia,
            @RequestParam("metadataArchivo") String metadataArchivo
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        empresaCanFotografiaService.guardarCanFotografia(
                vehiculoUuid,
                username,
                fotografia,
                new Gson().fromJson(metadataArchivo, CanFotografiaMetadata.class)
        );
    }

    @DeleteMapping(value = CAN_FOTOGRAFIAS_URI + "/{fotografiaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void eliminarFotografiaCan(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "fotografiaUuid") String fotografiaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        empresaCanFotografiaService.eliminarCanFotografia(canUuid, fotografiaUuid, username);
    }
}
