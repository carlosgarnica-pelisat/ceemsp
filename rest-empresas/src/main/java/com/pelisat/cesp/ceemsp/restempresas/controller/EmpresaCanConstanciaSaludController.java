package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanConstanciaSaludDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaCanConstanciaSaludService;
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
public class EmpresaCanConstanciaSaludController {
    private final EmpresaCanConstanciaSaludService empresaCanConstanciaSaludService;
    private final JwtUtils jwtUtils;
    private static final String CAN_CONSTANCIA_URI = "/canes/{canUuid}/constancias";

    @Autowired
    public EmpresaCanConstanciaSaludController(EmpresaCanConstanciaSaludService empresaCanConstanciaSaludService,
                                               JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaCanConstanciaSaludService = empresaCanConstanciaSaludService;
    }

    @GetMapping(value = CAN_CONSTANCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanConstanciaSaludDto> obtenerCanesConstanciasSaludPorCanUuid(
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return empresaCanConstanciaSaludService.obtenerConstanciasSaludPorCanUuid(canUuid);
    }

    @GetMapping(value = CAN_CONSTANCIA_URI + "/{constanciaUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarConstanciaPdf(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "constanciaUuid") String constanciaUuid
    ) throws Exception {
        File file = empresaCanConstanciaSaludService.obtenerPdfConstanciaSalud(canUuid, constanciaUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = CAN_CONSTANCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanConstanciaSaludDto guardarCanConstanciaSalud(
            @PathVariable(value = "canUuid") String canUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("constanciaSalud") String constanciaSalud,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanConstanciaSaludService.guardarConstanciaSalud(canUuid, username, new Gson().fromJson(constanciaSalud, CanConstanciaSaludDto.class), archivo);
    }

    @PutMapping(value = CAN_CONSTANCIA_URI + "/{constanciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanConstanciaSaludDto modificarCanConstanciaSalud(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "constanciaUuid") String constanciaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("constanciaSalud") String constanciaSalud,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanConstanciaSaludService.modificarConstanciaSalud(canUuid, constanciaUuid, username, new Gson().fromJson(constanciaSalud, CanConstanciaSaludDto.class), archivo);
    }

    @DeleteMapping(value = CAN_CONSTANCIA_URI + "/{constanciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanConstanciaSaludDto eliminarCanConstanciaSalud(
            @PathVariable(value = "canUuid") String canUuid,
            @PathVariable(value = "constanciaUuid") String constanciaUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaCanConstanciaSaludService.eliminarConstanciaSalud(canUuid, constanciaUuid, username);
    }
}
