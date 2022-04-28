package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaDomicilioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping(value = EMPRESA_CANES_URI + "/{canUuid}")
    public CanDto obtenerCanPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canService.obtenerCanPorUuid(empresaUuid, canUuid, false);
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
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("can") String can,
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.eliminarCan(empresaUuid, canUuid, username, new Gson().fromJson(can, CanDto.class), archivo);
    }
}
