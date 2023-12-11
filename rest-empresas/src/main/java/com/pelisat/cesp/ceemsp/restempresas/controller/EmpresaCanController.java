package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaCanService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaCanController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_CANES_URI = "/canes";
    private final EmpresaCanService canService;

    @Autowired
    public EmpresaCanController(EmpresaCanService canService,
                                JwtUtils jwtUtils) {
        this.canService = canService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_CANES_URI)
    public List<CanDto> obtenerCanesPorEmpresaUuid(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.obtenerCanesPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/instalaciones")
    public List<CanDto> obtenerCanesEnInstalaciones(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.obtenerCanesEnInstalacionesPorEmpresa(username);
    }

    @GetMapping(value = EMPRESA_CANES_URI + "/{canUuid}")
    public CanDto obtenerCanPorUuid(
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canService.obtenerCanPorUuid(canUuid, false);
    }

    @PostMapping(value = EMPRESA_CANES_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CanDto guardarCan(
            @RequestBody CanDto canDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.guardarCan(username, canDto);
    }

    @PutMapping(value = EMPRESA_CANES_URI + "/{canUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanDto modificarCan(
            @RequestBody CanDto canDto,
            HttpServletRequest request,
            @PathVariable(value = "canUuid") String canUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.modificarCan(canUuid, username, canDto);
    }

    @PutMapping(value = EMPRESA_CANES_URI + "/{canUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CanDto eliminarCan(
            HttpServletRequest request,
            @PathVariable(value = "canUuid") String canUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("can") String can
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canService.eliminarCan(canUuid, username, new Gson().fromJson(can, CanDto.class), archivo);
    }
}
