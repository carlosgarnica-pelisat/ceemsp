package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraApoderadoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.apache.commons.io.FilenameUtils;
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
public class EmpresaEscrituraApoderadoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_APODERADOS_URI = "/empresas/{empresaUuid}/escrituras/{escrituraUuid}/apoderados";
    private final EmpresaEscrituraApoderadoService empresaEscrituraApoderadoService;

    @Autowired
    public EmpresaEscrituraApoderadoController(EmpresaEscrituraApoderadoService empresaEscrituraApoderadoService,
                                           JwtUtils jwtUtils) {
        this.empresaEscrituraApoderadoService = empresaEscrituraApoderadoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_APODERADOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraApoderadoService.obtenerApoderadosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_APODERADOS_URI + "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraApoderadoDto> obtenerEscrituraApoderadoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraApoderadoService.obtenerTodosApoderadosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_APODERADOS_URI + "/{socioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto obtenerEscrituraApoderadoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String socioUuid
    ) {
        return null;
        //return empresaEscrituraApoderadoService.obtenerRepresentantePorUuid(empresaUuid, escrituraUuid, socioUuid, false);
    }

    @PostMapping(value = EMPRESA_APODERADOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto crearApoderado(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.crearApoderado(empresaUuid, escrituraUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_APODERADOS_URI + "/{apoderadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto modificarApoderado(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String apoderadoUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.modificarApoderado(empresaUuid, escrituraUuid, apoderadoUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_APODERADOS_URI + "/{apoderadoUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaEscrituraApoderadoDto eliminarApoderado(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String apoderadoUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("apoderado") String apoderado,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraApoderadoService.eliminarApoderado(empresaUuid, escrituraUuid, apoderadoUuid, username, new Gson().fromJson(apoderado, EmpresaEscrituraApoderadoDto.class), archivo);
    }

    @GetMapping(value = EMPRESA_APODERADOS_URI + "/{apoderadoUuid}/documentos/fundatorios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> obtenerDocumentoFundatorio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String apoderadoUuid
    ) throws Exception {
        File file = empresaEscrituraApoderadoService.obtenerDocumentoFundatorioBajaApoderado(empresaUuid, escrituraUuid, apoderadoUuid);
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
}
