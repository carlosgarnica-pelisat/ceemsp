package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaLicenciaColectivaService;
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
public class EmpresaLicenciaColectivaController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_LICENCIAS_URI = "/licencias";
    private final EmpresaLicenciaColectivaService empresaLicenciaColectivaService;

    @Autowired
    public EmpresaLicenciaColectivaController(JwtUtils jwtUtils, EmpresaLicenciaColectivaService empresaLicenciaColectivaService) {
        this.empresaLicenciaColectivaService = empresaLicenciaColectivaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_LICENCIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaLicenciaColectivaDto> obtenerLicenciasColectivasPorEmpresa(
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.obtenerLicenciasColectivasPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaLicenciaColectivaDto obtenerLicenciaColectiva(
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) {
        return empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(licenciaUuid, false);
    }

    @GetMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarLicenciaPdf(
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) throws Exception {
        File file = empresaLicenciaColectivaService.descargarLicenciaPdf(licenciaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.setContentLength(file.length());
        httpHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = EMPRESA_LICENCIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaLicenciaColectivaDto guardarLicenciaColectiva(
            HttpServletRequest httpServletRequest,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("licencia") String licencia
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.guardarLicenciaColectiva(username, new Gson().fromJson(licencia, EmpresaLicenciaColectivaDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaLicenciaColectivaDto modificarLicenciaColectiva(
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("licencia") String licencia
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.modificarLicenciaColectiva(licenciaUuid, username, new Gson().fromJson(licencia, EmpresaLicenciaColectivaDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaLicenciaColectivaDto eliminarLicenciaColectiva(
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("licencia") String licencia,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.eliminarLicenciaColectiva(licenciaUuid, username, new Gson().fromJson(licencia, EmpresaLicenciaColectivaDto.class), archivo);
    }
}
