package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEscrituraSocioService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaLegalSociosController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_SOCIOS_URI = "/escrituras/{escrituraUuid}/socios";
    private final EmpresaEscrituraSocioService empresaEscrituraSocioService;

    @Autowired
    public EmpresaLegalSociosController(EmpresaEscrituraSocioService empresaEscrituraSocioService,
                                           JwtUtils jwtUtils) {
        this.empresaEscrituraSocioService = empresaEscrituraSocioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_SOCIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraSocioDto> obtenerSociosPorEscritura(
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraSocioService.obtenerSociosPorEscritura(escrituraUuid);
    }

    @PostMapping(value = EMPRESA_SOCIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraSocioDto crearEscrituraSocio(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraSocioDto empresaEscrituraSocioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraSocioService.crearSocio(escrituraUuid, username, empresaEscrituraSocioDto);
    }

    @PutMapping(value = EMPRESA_SOCIOS_URI + "/{socioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraSocioDto modificarEscrituraSocio(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "socioUuid") String socioUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraSocioDto empresaEscrituraSocioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraSocioService.modificarSocio(escrituraUuid, socioUuid, username, empresaEscrituraSocioDto);
    }

    @DeleteMapping(value = EMPRESA_SOCIOS_URI + "/{socioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraSocioDto eliminarEscrituraSocio(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "socioUuid") String socioUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraSocioService.eliminarSocio(escrituraUuid, socioUuid, username);
    }
}
