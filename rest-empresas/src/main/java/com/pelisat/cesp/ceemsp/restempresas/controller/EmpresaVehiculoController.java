package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaVehiculoService;
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
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.obtenerVehiculos(username);
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI + "/instalaciones")
    public List<VehiculoDto> obtenerVehiculosEnInstalaciones(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaVehiculoService.obtenerVehiculosEnInstalacionesPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto obtenerVehiculoPorUuid(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid
    ) {
        return empresaVehiculoService.obtenerVehiculoPorUuid(vehiculoUuid);
    }

    @PostMapping(value = EMPRESA_VEHICULOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehiculoDto guardarVehiculo(
            @RequestParam(value = "constanciaBlindaje", required = false) MultipartFile constanciaBlindaje,
            @RequestParam(value = "vehiculo") String vehiculo,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.guardarVehiculo(username, new Gson().fromJson(vehiculo, VehiculoDto.class), constanciaBlindaje);
    }

    @PutMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehiculoDto modificarVehiculo(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @RequestParam(value = "constanciaBlindaje", required = false) MultipartFile constanciaBlindaje,
            @RequestParam(value = "vehiculo") String vehiculo,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.modificarVehiculo(vehiculoUuid, username, new Gson().fromJson(vehiculo, VehiculoDto.class), constanciaBlindaje);
    }

    @PutMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehiculoDto eliminarVehiculo(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("vehiculo") String vehiculo,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.eliminarVehiculo(vehiculoUuid, username, new Gson().fromJson(vehiculo, VehiculoDto.class), archivo);
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarEscrituraPdf(
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid
    ) throws Exception {
        File file = empresaVehiculoService.obtenerConstanciaBlindaje(vehiculoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }
}
