package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaFormaEjecucionService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaFormaEjecucionController {
    private final EmpresaFormaEjecucionService empresaFormaEjecucionService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_FORMAS_EJECUCION_URI = "/formas-ejecucion";

    @Autowired
    public EmpresaFormaEjecucionController(EmpresaFormaEjecucionService empresaFormaEjecucionService,
                                           JwtUtils jwtUtils) {
        this.empresaFormaEjecucionService = empresaFormaEjecucionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_FORMAS_EJECUCION_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaFormaEjecucionDto> obtenerFormasEjecucionEmpresa(
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaFormaEjecucionService.obtenerFormasEjecucionEmpresa(username);
    }
}
