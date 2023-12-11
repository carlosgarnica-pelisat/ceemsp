package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaCanAdiestramientoService;
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
public class EmpresaCanAdiestramientoController {
    private final EmpresaCanAdiestramientoService empresaCanAdiestramientoService;
    private final JwtUtils jwtUtils;
    private static final String CAN_ADIESTRAMIENTOS_URI = "/canes/{canUuid}/adiestramientos";

    @Autowired
    public EmpresaCanAdiestramientoController(EmpresaCanAdiestramientoService empresaCanAdiestramientoService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaCanAdiestramientoService = empresaCanAdiestramientoService;
    }

    @GetMapping(value = CAN_ADIESTRAMIENTOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanAdiestramientoDto> obtenerCanesAdiestramientoPorCanUuid(
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return empresaCanAdiestramientoService.obtenerAdiestramientosPorCanUuid(canUuid);
    }

    @GetMapping(value = CAN_ADIESTRAMIENTOS_URI + "/{adiestramientoUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarEscrituraPdf(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "adiestramientoUuid") String adiestramientoUuid
    ) throws Exception {
        File file = empresaCanAdiestramientoService.obtenerAdiestramientoArchivo(canUuid, adiestramientoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = CAN_ADIESTRAMIENTOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanAdiestramientoDto guardarCanAdiestramiento(
            @PathVariable(value = "canUuid") String canUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("adiestramiento") String adiestramiento,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanAdiestramientoService.guardarCanAdiestramiento(canUuid, username, new Gson().fromJson(adiestramiento, CanAdiestramientoDto.class), archivo);
    }

    @PutMapping(value = CAN_ADIESTRAMIENTOS_URI + "/{adiestramientoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanAdiestramientoDto modificarCanAdiestramiento(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "adiestramientoUuid") String adiestramientoUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("adiestramiento") String adiestramiento,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanAdiestramientoService.modificarCanAdiestramiento(canUuid, adiestramientoUuid, username, new Gson().fromJson(adiestramiento, CanAdiestramientoDto.class), archivo);
    }

    @DeleteMapping(value = CAN_ADIESTRAMIENTOS_URI + "/{adiestramientoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanAdiestramientoDto eliminarCanAdiestramiento(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "adiestramientoUuid") String adiestramientoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanAdiestramientoService.eliminarCanAdiestramiento(canUuid, adiestramientoUuid, username);
    }
}
