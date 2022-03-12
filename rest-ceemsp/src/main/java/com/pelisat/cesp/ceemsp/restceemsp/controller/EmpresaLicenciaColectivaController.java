package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaLicenciaColectivaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
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
    private static final String EMPRESA_LICENCIAS_URI = "/empresas/{empresaUuid}/licencias";
    private final EmpresaLicenciaColectivaService empresaLicenciaColectivaService;

    @Autowired
    public EmpresaLicenciaColectivaController(JwtUtils jwtUtils, EmpresaLicenciaColectivaService empresaLicenciaColectivaService) {
        this.empresaLicenciaColectivaService = empresaLicenciaColectivaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_LICENCIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaLicenciaColectivaDto> obtenerLicenciasColectivasPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaLicenciaColectivaService.obtenerLicenciasColectivasPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaLicenciaColectivaDto obtenerLicenciaColectiva(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) {
        return empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(empresaUuid, licenciaUuid, false);
    }

    @GetMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarLicenciaPdf(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) throws Exception {
        File file = empresaLicenciaColectivaService.descargarLicenciaPdf(empresaUuid, licenciaUuid);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.setContentLength(file.length());
        httpHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = EMPRESA_LICENCIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaLicenciaColectivaDto guardarLicenciaColectiva(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            HttpServletRequest httpServletRequest,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("licencia") String licencia
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.guardarLicenciaColectiva(empresaUuid, username, new Gson().fromJson(licencia, EmpresaLicenciaColectivaDto.class), archivo);
    }

    @PutMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaLicenciaColectivaDto modificarLicenciaColectiva(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            HttpServletRequest httpServletRequest,
            @RequestBody EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.modificarLicenciaColectiva(empresaUuid, licenciaUuid, username, empresaLicenciaColectivaDto);
    }

    @DeleteMapping(value = EMPRESA_LICENCIAS_URI + "/{licenciaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaLicenciaColectivaDto eliminarLicenciaColectiva(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.eliminarLicenciaColectiva(empresaUuid, licenciaUuid, username);
    }
}
