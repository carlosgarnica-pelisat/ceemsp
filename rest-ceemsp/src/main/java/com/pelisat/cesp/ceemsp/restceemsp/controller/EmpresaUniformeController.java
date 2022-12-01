package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaUniformeService;
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
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaUniformeController {
    private final EmpresaUniformeService empresaUniformeService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_UNIFORMES_URI = "/empresas/{empresaUuid}/uniformes";

    @Autowired
    public EmpresaUniformeController(EmpresaUniformeService empresaUniformeService, JwtUtils jwtUtils) {
        this.empresaUniformeService = empresaUniformeService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_UNIFORMES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaUniformeDto> obtenerEmpresaUniformes(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaUniformeService.obtenerUniformesPorEmpresaUuid(empresaUuid);
    }

    @GetMapping(value = EMPRESA_UNIFORMES_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto obtenerEmpresaUniformePorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return empresaUniformeService.obtenerUniformePorUuid(empresaUuid, uniformeUuid);
    }

    @GetMapping(value = EMPRESA_UNIFORMES_URI + "/{uniformeUuid}/fotos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarFotoUniforme(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) throws FileNotFoundException {
        File file = empresaUniformeService.descargarFotoUniforme(empresaUuid, uniformeUuid);
        HttpHeaders responseHeaders = new HttpHeaders();
        MediaType mediaType = null;

        switch(FilenameUtils.getExtension(file.getName())) {
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

    @PostMapping(value = EMPRESA_UNIFORMES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaUniformeDto guardarUniforme(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("uniforme") String uniforme
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.guardarUniforme(empresaUuid, username, new Gson().fromJson(uniforme, EmpresaUniformeDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_UNIFORMES_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaUniformeDto modificarUniforme(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("uniforme") String uniforme
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.modificarUniforme(empresaUuid, uniformeUuid, username, new Gson().fromJson(uniforme, EmpresaUniformeDto.class), archivo);
    }

    @DeleteMapping(value = EMPRESA_UNIFORMES_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto eliminarUniforme(
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.eliminarUniforme(empresaUuid, uniformeUuid, username);
    }
}
