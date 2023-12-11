package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaCanCartillaVacunacionService;
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
public class EmpresaCanCartillaVacunacionController {
    private final EmpresaCanCartillaVacunacionService empresaCanCartillaVacunacionService;
    private final JwtUtils jwtUtils;
    private static final String CAN_CARTILLA_URI = "/canes/{canUuid}/cartillas";

    @Autowired
    public EmpresaCanCartillaVacunacionController(
            EmpresaCanCartillaVacunacionService empresaCanCartillaVacunacionService, JwtUtils jwtUtils
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaCanCartillaVacunacionService = empresaCanCartillaVacunacionService;
    }

    @GetMapping(value = CAN_CARTILLA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanCartillaVacunacionDto> obtenerCanesCartillasPorCanUuid(
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return empresaCanCartillaVacunacionService.obtenerCartillasVacunacionPorCanUuid(canUuid);
    }

    @GetMapping(value = CAN_CARTILLA_URI + "/{cartillaUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarEscrituraPdf(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "cartillaUuid") String cartillaUuid
    ) throws Exception {
        File file = empresaCanCartillaVacunacionService.obtenerPdfCartillaVacunacion(canUuid, cartillaUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = CAN_CARTILLA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanCartillaVacunacionDto guardarCanCartillaVacunacion(
            @PathVariable(value = "canUuid") String canUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("cartillaVacunacion") String cartillaVacunacion,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanCartillaVacunacionService.guardarCartillaVacunacion(canUuid, username, new Gson().fromJson(cartillaVacunacion, CanCartillaVacunacionDto.class), archivo);
    }

    @PutMapping(value = CAN_CARTILLA_URI + "/{cartillaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanCartillaVacunacionDto modificarCanCartillaVacunacion(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "cartillaUuid") String cartillaUuid,
            HttpServletRequest request,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("cartillaVacunacion") String cartillaVacunacion
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanCartillaVacunacionService.modificarCartillaVacunacion(canUuid, cartillaUuid, username, new Gson().fromJson(cartillaVacunacion, CanCartillaVacunacionDto.class), archivo);
    }

    @DeleteMapping(value = CAN_CARTILLA_URI + "/{cartillaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanCartillaVacunacionDto eliminarCanCartillaVacunacion(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "cartillaUuid") String cartillaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanCartillaVacunacionService.borrarCartillaVacunacion(canUuid, cartillaUuid, username);
    }
}
