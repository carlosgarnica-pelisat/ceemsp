package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaLicenciaColectivaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping(value = EMPRESA_LICENCIAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaLicenciaColectivaDto guardarLicenciaColectiva(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            HttpServletRequest httpServletRequest,
            @RequestBody EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaService.guardarLicenciaColectiva(empresaUuid, username, empresaLicenciaColectivaDto);
    }
}
