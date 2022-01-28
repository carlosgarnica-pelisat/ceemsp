package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaVehiculoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_VEHICULOS_URI = "/vehiculos";
    private final EmpresaVehiculoService empresaVehiculoService;

    @Autowired
    public EmpresaVehiculoController(
            JwtUtils jwtUtils,
            EmpresaVehiculoService empresaVehiculoService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaVehiculoService = empresaVehiculoService;
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoDto> obtenerVehiculosPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaVehiculoService.obtenerVehiculos(empresaUuid);
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto obtenerVehiculoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid
    ) {
        return empresaVehiculoService.obtenerVehiculoPorUuid(empresaUuid, vehiculoUuid);
    }

    @PostMapping(value = EMPRESA_VEHICULOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto guardarVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody VehiculoDto vehiculoDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.guardarVehiculo(username, vehiculoDto);
    }
}
