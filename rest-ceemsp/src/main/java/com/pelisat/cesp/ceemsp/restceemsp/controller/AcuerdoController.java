package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.AcuerdoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.AcuerdoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.ArmaService;
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
public class AcuerdoController {
    private final AcuerdoService acuerdoService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_ACUERDO_URI = "/empresas/{empresaUuid}/acuerdos";

    @Autowired
    public AcuerdoController(AcuerdoService acuerdoService, JwtUtils jwtUtils) {
        this.acuerdoService = acuerdoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_ACUERDO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AcuerdoDto> obtenerAcuerdosEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return acuerdoService.obtenerAcuerdosEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_ACUERDO_URI + "/{acuerdoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AcuerdoDto obtenerAcuerdoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "acuerdoUuid") String acuerdoUuid
    ) {
        return acuerdoService.obtenerAcuerdoPorUuid(empresaUuid, acuerdoUuid);
    }

    @GetMapping(value = EMPRESA_ACUERDO_URI + "/{acuerdoUuid}/archivo")
    public ResponseEntity<InputStreamResource> obtenerArchivo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "acuerdoUuid") String acuerdoUuid
    ) throws Exception {
        File file = acuerdoService.obtenerArchivoAcuerdo(empresaUuid, acuerdoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = EMPRESA_ACUERDO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AcuerdoDto guardarAcuerdo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestParam(value = "archivo") MultipartFile archivo,
            @RequestParam("acuerdo") String acuerdo,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return acuerdoService.guardarAcuerdo(empresaUuid, new Gson().fromJson(acuerdo, AcuerdoDto.class), username, archivo);
    }

    @PutMapping(value = EMPRESA_ACUERDO_URI + "/{acuerdoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AcuerdoDto modificarAcuerdo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "acuerdoUuid") String acuerdoUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("acuerdo") String acuerdo,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return acuerdoService.modificarAcuerdo(empresaUuid, acuerdoUuid, new Gson().fromJson(acuerdo, AcuerdoDto.class), username, archivo);
    }

    @PutMapping(value = EMPRESA_ACUERDO_URI + "/{acuerdoUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE)
    public AcuerdoDto eliminarAcuerdo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "acuerdoUuid") String acuerdoUuid,
            HttpServletRequest request,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("acuerdo") String acuerdo
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return acuerdoService.eliminarAcuerdo(empresaUuid, acuerdoUuid, username, new Gson().fromJson(acuerdo, AcuerdoDto.class), archivo);
    }
}
