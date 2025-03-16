package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.metadata.VehiculoFotografiaMetadata;
import com.pelisat.cesp.ceemsp.restceemsp.service.VehiculoFotografiaService;
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
public class VehiculoFotografiaController {
    private final VehiculoFotografiaService vehiculoFotografiaService;
    private final JwtUtils jwtUtils;
    private static final String VEHICULO_FOTOGRAFIAS_URI = "/empresas/{empresaUuid}/vehiculos/{vehiculoUuid}/fotografias";

    @Autowired
    public VehiculoFotografiaController(VehiculoFotografiaService vehiculoFotografiaService, JwtUtils jwtUtils) {
        this.vehiculoFotografiaService = vehiculoFotografiaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = VEHICULO_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoFotografiaMetadata> listarFotografiasPorPersonal(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String personaUuid
    ) {
        return vehiculoFotografiaService.mostrarVehiculoFotografias(empresaUuid, personaUuid);
    }

    @GetMapping(value = VEHICULO_FOTOGRAFIAS_URI + "/{fotografiaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarFotografiaVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @PathVariable(value = "fotografiaUuid") String fotografiaUuid
    ) throws Exception {
        File file = vehiculoFotografiaService.descargarFotografiaVehiculo(empresaUuid, vehiculoUuid, fotografiaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG); // TODO: Validar el tipo de imagen en funcion de su formato
        httpHeaders.setContentDispositionFormData("attachment",  file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<InputStreamResource>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = VEHICULO_FOTOGRAFIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void guardarFotografiaElemento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            HttpServletRequest request,
            @RequestParam("fotografia") MultipartFile fotografia,
            @RequestParam("metadataArchivo") String metadataArchivo
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        vehiculoFotografiaService.guardarVehiculoFotografia(
                empresaUuid,
                vehiculoUuid,
                username,
                fotografia,
                new Gson().fromJson(metadataArchivo, VehiculoFotografiaMetadata.class)
        );
    }

    @DeleteMapping(value = VEHICULO_FOTOGRAFIAS_URI + "/{fotografiaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void eliminarFotografiaElemento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @PathVariable(value = "fotografiaUuid") String fotografiaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        vehiculoFotografiaService.eliminarVehiculoFotografia(empresaUuid, vehiculoUuid, fotografiaUuid, username);
    }
}
