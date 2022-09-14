package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaLicenciaColectivaDomicilioService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaLicenciaColectivaDomicilioController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_LICENCIA_DOMICILIOS_URI = "/licencias/{licenciaUuid}/domicilios";
    private final EmpresaLicenciaColectivaDomicilioService empresaLicenciaColectivaDomicilioService;

    @Autowired
    public EmpresaLicenciaColectivaDomicilioController(EmpresaLicenciaColectivaDomicilioService empresaLicenciaColectivaDomicilioService,
                                                       JwtUtils jwtUtils) {
        this.empresaLicenciaColectivaDomicilioService = empresaLicenciaColectivaDomicilioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_LICENCIA_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaDomicilioDto> obtenerDomiciliosPorLicenciaColectiva(
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) {
        return empresaLicenciaColectivaDomicilioService.obtenerDomiciliosPorLicenciaColectiva(licenciaUuid);
    }

    @PostMapping(value = EMPRESA_LICENCIA_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioDto guardarDomicilioEnLicenciaColectiva(
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            HttpServletRequest httpServletRequest,
            @RequestBody EmpresaDomicilioDto empresaDomicilioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaDomicilioService.guardarDomicilioEnLicenciaColectiva(licenciaUuid, username, empresaDomicilioDto);
    }

    @DeleteMapping(value = EMPRESA_LICENCIA_DOMICILIOS_URI + "/{domicilioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioDto eliminarDomicilioEnLicenciaColectiva(
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaLicenciaColectivaDomicilioService.eliminarDomicilioEnLicenciaColectiva(licenciaUuid, domicilioUuid, username);
    }
}
