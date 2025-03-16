package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.AcuerdoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaAcuerdoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaAcuerdoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_ACUERDO_URI = "/acuerdos";
    private final EmpresaAcuerdoService empresaAcuerdoService;

    @Autowired
    public EmpresaAcuerdoController(
            EmpresaAcuerdoService empresaAcuerdoService, JwtUtils jwtUtils
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaAcuerdoService = empresaAcuerdoService;
    }

    @GetMapping(value = EMPRESA_ACUERDO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AcuerdoDto> obtenerAcuerdosEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaAcuerdoService.obtenerAcuerdosEmpresa(username);
    }

    @GetMapping(value = EMPRESA_ACUERDO_URI + "/{acuerdoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AcuerdoDto obtenerAcuerdoPorUuid(
            @PathVariable(value = "acuerdoUuid") String acuerdoUuid
    ) {
        return empresaAcuerdoService.obtenerAcuerdoPorUuid(acuerdoUuid);
    }

    @GetMapping(value = EMPRESA_ACUERDO_URI + "/{acuerdoUuid}/archivo")
    public ResponseEntity<InputStreamResource> obtenerArchivo(
            @PathVariable(value = "acuerdoUuid") String acuerdoUuid
    ) throws Exception {
        File file = empresaAcuerdoService.obtenerArchivoAcuerdo(acuerdoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

}
