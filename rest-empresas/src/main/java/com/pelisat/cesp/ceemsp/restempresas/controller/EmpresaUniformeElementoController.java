package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaUniformeElementoService;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaUniformeService;
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
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaUniformeElementoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_UNIFORME_ELEMENTO_URI = "/uniformes/{uniformeUuid}/{elementos}";
    private final EmpresaUniformeElementoService empresaUniformeElementoService;

    @Autowired
    public EmpresaUniformeElementoController(EmpresaUniformeElementoService empresaUniformeElementoService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.empresaUniformeElementoService = empresaUniformeElementoService;
    }

    @GetMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaUniformeElementoDto> obtenerUniformesPorEmpresa(
            HttpServletRequest request,
            @PathVariable(value = "uniformeUuid") String uniformeUuid
    ) {
        return empresaUniformeElementoService.obtenerElementosUniformePorEmpresaUuid(uniformeUuid);
    }

    @GetMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI + "/{elementoUuid}/descargar")
    public ResponseEntity<InputStreamResource> descargarFotografia(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @PathVariable(value = "elementoUuid") String elementoUuid
    ) throws FileNotFoundException {
        File file = empresaUniformeElementoService.obtenerArchivoUniforme(uniformeUuid, elementoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.IMAGE_JPEG);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaUniformeElementoDto crearUniformeElemento(
            HttpServletRequest request,
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("elemento") String elemento
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeElementoService.guardarUniformeElemento(uniformeUuid, username, new Gson().fromJson(elemento, EmpresaUniformeElementoDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI + "/{elementoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaUniformeElementoDto modificarUniformeElemento(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @PathVariable(value = "elementoUuid") String elementoUuid,
            HttpServletRequest request,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("elemento") String elemento
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeElementoService.modificarUniformeElemento(uniformeUuid, elementoUuid, username, new Gson().fromJson(elemento, EmpresaUniformeElementoDto.class), archivo);
    }

    @DeleteMapping(value = EMPRESA_UNIFORME_ELEMENTO_URI + "/{elementoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaUniformeElementoDto eliminarUniforme(
            @PathVariable(value = "uniformeUuid") String uniformeUuid,
            @PathVariable(value = "elementoUuid") String elementoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaUniformeElementoService.eliminarUniformeElemento(uniformeUuid, elementoUuid, username);
    }
}
