package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanConstanciaSaludDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanConstanciaSaludService;
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
public class CanConstanciaSaludController {
    private final CanConstanciaSaludService canConstanciaSaludService;
    private final JwtUtils jwtUtils;
    private static final String CAN_CONSTANCIA_URI = "/empresas/{empresaUuid}/canes/{canUuid}/constancias";

    @Autowired
    public CanConstanciaSaludController(CanConstanciaSaludService canConstanciaSaludService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.canConstanciaSaludService = canConstanciaSaludService;
    }

    @GetMapping(value = CAN_CONSTANCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanConstanciaSaludDto> obtenerCanesConstanciasSaludPorCanUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canConstanciaSaludService.obtenerConstanciasSaludPorCanUuid(empresaUuid, canUuid);
    }

    @GetMapping(value = CAN_CONSTANCIA_URI + "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanConstanciaSaludDto> obtenerTodasCanesConstanciasSaludPorCanUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canConstanciaSaludService.obtenerTodasConstanciasSaludPorCanUuid(empresaUuid, canUuid);
    }

    @GetMapping(value = CAN_CONSTANCIA_URI + "/{constanciaUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarEscrituraPdf(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "constanciaUuid") String constanciaUuid
    ) throws Exception {
        File file = canConstanciaSaludService.obtenerPdfConstanciaSalud(empresaUuid, canUuid, constanciaUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = CAN_CONSTANCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanConstanciaSaludDto guardarCanConstanciaSalud(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("constanciaSalud") String constanciaSalud,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canConstanciaSaludService.guardarConstanciaSalud(empresaUuid, canUuid, username, new Gson().fromJson(constanciaSalud, CanConstanciaSaludDto.class), archivo);
    }

    @PutMapping(value = CAN_CONSTANCIA_URI + "/{constanciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanConstanciaSaludDto modificarCanConstanciaSalud(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "constanciaUuid") String constanciaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("constanciaSalud") String constanciaSalud,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canConstanciaSaludService.modificarConstanciaSalud(empresaUuid, canUuid, constanciaUuid, username, new Gson().fromJson(constanciaSalud, CanConstanciaSaludDto.class), archivo);
    }

    @DeleteMapping(value = CAN_CONSTANCIA_URI + "/{constanciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanConstanciaSaludDto eliminarCanConstanciaSalud(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "constanciaUuid") String constanciaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canConstanciaSaludService.eliminarConstanciaSalud(empresaUuid, canUuid, constanciaUuid, username);
    }
}
