package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEscrituraService;
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
public class EmpresaLegalController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_ESCRITURA_URI = "/escrituras";
    private final EmpresaEscrituraService empresaEscrituraService;

    @Autowired
    public EmpresaLegalController(EmpresaEscrituraService empresaEscrituraService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaEscrituraService = empresaEscrituraService;
    }

    @GetMapping(value = EMPRESA_ESCRITURA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraDto> obtenerEscriturasPorEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraService.obtenerEscrituras(username);
    }

    @GetMapping(value = EMPRESA_ESCRITURA_URI + "/{escrituraUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraDto obtenerEscrituraPorUuid(
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraService.obtenerEscrituraPorUuid(escrituraUuid, false);
    }

    @GetMapping(value = EMPRESA_ESCRITURA_URI + "/{escrituraUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarEscrituraPdf(
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) throws Exception {
        File file = empresaEscrituraService.obtenerEscrituraPdf(escrituraUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }
}
