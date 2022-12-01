package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaVehiculoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_VEHICULOS_URI = "/empresas/{empresaUuid}/vehiculos";
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
        return empresaVehiculoService.obtenerVehiculosPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI + "/eliminados")
    public List<VehiculoDto> obtenerVehiculosEliminadosPorEmpresa(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return empresaVehiculoService.obtenerVehiculosEliminadosPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoDto obtenerVehiculoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid
    ) {
        return empresaVehiculoService.obtenerVehiculoPorUuid(empresaUuid, vehiculoUuid, false);
    }

    @GetMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}/pdf")
    public ResponseEntity<InputStreamResource> descargarEscrituraPdf(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid
    ) throws Exception {
        File file = empresaVehiculoService.obtenerConstanciaBlindaje(empresaUuid, vehiculoUuid);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = EMPRESA_VEHICULOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehiculoDto guardarVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestParam(value = "constanciaBlindaje", required = false) MultipartFile constanciaBlindaje,
            @RequestParam(value = "vehiculo") String vehiculo,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.guardarVehiculo(empresaUuid, username, new Gson().fromJson(vehiculo, VehiculoDto.class), constanciaBlindaje);
    }

    @PutMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehiculoDto modificarVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @RequestParam(value = "constanciaBlindaje", required = false) MultipartFile constanciaBlindaje,
            @RequestParam(value = "vehiculo") String vehiculo,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.modificarVehiculo(empresaUuid, vehiculoUuid, username, new Gson().fromJson(vehiculo, VehiculoDto.class), constanciaBlindaje);
    }

    @PutMapping(value = EMPRESA_VEHICULOS_URI + "/{vehiculoUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehiculoDto eliminarVehiculo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "vehiculoUuid") String vehiculoUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("vehiculo") String vehiculo,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaVehiculoService.eliminarVehiculo(empresaUuid, vehiculoUuid, username, new Gson().fromJson(vehiculo, VehiculoDto.class), archivo);
    }
}
