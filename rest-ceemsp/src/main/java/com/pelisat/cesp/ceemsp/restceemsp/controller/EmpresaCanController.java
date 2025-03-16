package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalCanDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaDomicilioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
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
public class EmpresaCanController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_CANES_URI = "/empresas/{empresaUuid}/canes";
    private final CanService canService;

    @Autowired
    public EmpresaCanController(CanService canService,
                                      JwtUtils jwtUtils) {
        this.canService = canService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_CANES_URI)
    public List<CanDto> obtenerCanesPorEmpresaUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return canService.obtenerCanesPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/eliminados")
    public List<CanDto> obtenerCanesEliminadosPorEmpresaUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return canService.obtenerCanesEliminadosPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/instalaciones")
    public List<CanDto> obtenerCanesEnInstalaciones(
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) {
        return canService.obtenerCanesEnInstalacionesPorEmpresa(empresaUuid);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/{canUuid}")
    public CanDto obtenerCanPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canService.obtenerCanPorUuid(empresaUuid, canUuid, false);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/{canUuid}/movimientos/asignaciones")
    public List<PersonalCanDto> obtenerMovimientosPorCan(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canService.obtenerMovimientosCan(empresaUuid, canUuid);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/{canUuid}/movimientos/direcciones")
    public List<CanDomicilioDto> obtenerMovimientosDomicilioPorCan(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canService.obtenerMovimientosDomicilioCan(empresaUuid, canUuid);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/{canUuid}/documentos/fundatorios")
    public ResponseEntity<InputStreamResource> descargarDocumentoFundatorio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) throws Exception {
        File file = canService.descargarDocumentoFundatorio(empresaUuid, canUuid);

        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentDispositionFormData("attachment", file.getName());
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(value = EMPRESA_CANES_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CanDto guardarCan(
            @RequestBody CanDto canDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.guardarCan(empresaUuid, username, canDto);
    }

    @PutMapping(value = EMPRESA_CANES_URI + "/{canUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanDto modificarCan(
            @RequestBody CanDto canDto,
            HttpServletRequest request,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.modificarCan(empresaUuid, canUuid, username, canDto);
    }

    @PutMapping(value = EMPRESA_CANES_URI + "/{canUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanDto eliminarCan(
            HttpServletRequest request,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("can") String can,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.eliminarCan(empresaUuid, canUuid, username, new Gson().fromJson(can, CanDto.class), archivo);
    }
}
