package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVisitaService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaVisitaController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_VISITA_URI = "/empresas/{empresaUuid}/visitas";
    private final EmpresaVisitaService empresaVisitaService;

    @Autowired
    public EmpresaVisitaController(
            JwtUtils jwtUtils,
            EmpresaVisitaService empresaVisitaService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaVisitaService = empresaVisitaService;
    }

    @GetMapping(value = EMPRESA_VISITA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VisitaDto> obtenerVisitasPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaVisitaService.obtenerVisitasPorEmpresa(empresaUuid);
    }
}
