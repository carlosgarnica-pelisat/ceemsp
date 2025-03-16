package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEscrituraService;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaUniformeService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
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
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_UNIFORME_URI = "/uniformes";
    private final EmpresaUniformeService empresaUniformeService;

    @Autowired
    public EmpresaUniformeController(EmpresaUniformeService empresaUniformeService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaUniformeService = empresaUniformeService;
    }

    @GetMapping(value = EMPRESA_UNIFORME_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaUniformeDto> obtenerUniformesPorEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.obtenerUniformesPorEmpresaUuid(username);
    }

    @GetMapping(value = EMPRESA_UNIFORME_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto obtenerEscrituraPorUuid(
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return empresaUniformeService.obtenerUniformePorUuid(uniformeUuid);
    }

    @GetMapping(value = EMPRESA_UNIFORME_URI + "/{uniformeUuid}/fotos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> descargarFotoUniforme(
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) throws FileNotFoundException {
        File file = empresaUniformeService.descargarFotoUniforme(uniformeUuid);
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

    @PostMapping(value = EMPRESA_UNIFORME_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaUniformeDto crearUniforme(
            HttpServletRequest request,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("uniforme") String uniforme
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.guardarUniforme(username, new Gson().fromJson(uniforme, EmpresaUniformeDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_UNIFORME_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto modificarUniforme(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            HttpServletRequest request,
            @RequestBody EmpresaUniformeDto empresaUniformeDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.modificarUniforme(uniformeUuid, username, empresaUniformeDto);
    }

    @DeleteMapping(value = EMPRESA_UNIFORME_URI + "/{uniformeUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeDto eliminarUniforme(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeService.eliminarUniforme(uniformeUuid, username);
    }
}
