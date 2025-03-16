package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaController {
    private final EmpresaService empresaService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESAS_URI = "/empresas";

    @Autowired
    public EmpresaController(EmpresaService empresaService, JwtUtils jwtUtils) {
        this.empresaService = empresaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESAS_URI)
    public List<EmpresaDto> obtenerEmpresas(
            @RequestParam(value = "status", required = false) String status
    ) {
        if(StringUtils.isBlank(status)) {
            return empresaService.obtenerTodas();
        } else {
            return empresaService.obtenerPorStatus(EmpresaStatusEnum.valueOf(status));
        }

    }

    @PostMapping(value = EMPRESAS_URI, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaDto guardarEmpresa(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("empresa") String empresa,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaService.crearEmpresa(new Gson().fromJson(empresa, EmpresaDto.class), username, archivo, logo);
    }

    @GetMapping(value = EMPRESAS_URI + "/{empresaUuid}")
    public EmpresaDto obtenerEmpresaPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaService.obtenerPorUuid(empresaUuid);
    }

    @GetMapping(value = EMPRESAS_URI + "/{empresaUuid}/documentos/registro-federal")
    public ResponseEntity<InputStreamResource> obtenerRegistroFederal(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws FileNotFoundException {
        File file = empresaService.obtenerDocumentoRegistroFederal(empresaUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @GetMapping(value = EMPRESAS_URI + "/{empresaUuid}/logo")
    public ResponseEntity<InputStreamResource> obtenerLogo(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws FileNotFoundException {
        File file = empresaService.obtenerLogo(empresaUuid);

        HttpHeaders responseHeaders = new HttpHeaders();
        MediaType mediaType = null;

        switch(FilenameUtils.getExtension(file.getName())) {
            case "pdf":
                mediaType = MediaType.APPLICATION_PDF;
                break;
            case "jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
        }

        responseHeaders.setContentType(mediaType);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PutMapping(value = EMPRESAS_URI + "/{empresaUuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaDto modificarDatosEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            HttpServletRequest request,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam("empresa") String empresaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaService.modificarEmpresa(new Gson().fromJson(empresaDto, EmpresaDto.class), username, empresaUuid, archivo, logo);
    }

    @PutMapping(value = EMPRESAS_URI + "/{empresaUuid}/status")
    public EmpresaDto cambiarStatusEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            HttpServletRequest request,
            @RequestBody EmpresaDto empresaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaService.cambiarStatusEmpresa(empresaDto, username, empresaUuid);
    }
}
