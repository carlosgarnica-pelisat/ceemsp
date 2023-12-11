package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaTipoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanAdiestramientoService;
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
public class CanAdiestramientoController {
    private final CanAdiestramientoService canAdiestramientoService;
    private final JwtUtils jwtUtils;
    private static final String CAN_ADIESTRAMIENTO_URI = "/empresas/{empresaUuid}/canes/{canUuid}/adiestramientos";

    @Autowired
    public CanAdiestramientoController(CanAdiestramientoService canAdiestramientoService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.canAdiestramientoService = canAdiestramientoService;
    }

    @GetMapping(value = CAN_ADIESTRAMIENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanAdiestramientoDto> obtenerCanesAdiestramientoPorCanUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canAdiestramientoService.obtenerAdiestramientosPorCanUuid(empresaUuid, canUuid);
    }

    @GetMapping(value = CAN_ADIESTRAMIENTO_URI + "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanAdiestramientoDto> obtenerTodosCanesAdiestramientosPorCanUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canAdiestramientoService.obtenerTodosAdiestramientosPorCanUuid(empresaUuid, canUuid);
    }

    @GetMapping(value = CAN_ADIESTRAMIENTO_URI + "/{adiestramientoUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarEscrituraPdf(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "adiestramientoUuid") String adiestramientoUuid
    ) throws Exception {
        File file = canAdiestramientoService.obtenerAdiestramientoArchivo(empresaUuid, canUuid, adiestramientoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = CAN_ADIESTRAMIENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanAdiestramientoDto guardarCanAdiestramiento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("adiestramiento") String adiestramiento,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canAdiestramientoService.guardarCanAdiestramiento(empresaUuid, canUuid, username, new Gson().fromJson(adiestramiento, CanAdiestramientoDto.class), archivo);
    }

    @PutMapping(value = CAN_ADIESTRAMIENTO_URI + "/{adiestramientoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanAdiestramientoDto modificarCanAdiestramiento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "adiestramientoUuid") String adiestramientoUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("adiestramiento") String adiestramiento,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canAdiestramientoService.modificarCanAdiestramiento(empresaUuid, canUuid, adiestramientoUuid, username, new Gson().fromJson(adiestramiento, CanAdiestramientoDto.class), archivo);
    }

    @DeleteMapping(value = CAN_ADIESTRAMIENTO_URI + "/{adiestramientoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanAdiestramientoDto eliminarCanAdiestramiento(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "adiestramientoUuid") String adiestramientoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canAdiestramientoService.eliminarCanAdiestramiento(empresaUuid, canUuid, adiestramientoUuid, username);
    }

}
