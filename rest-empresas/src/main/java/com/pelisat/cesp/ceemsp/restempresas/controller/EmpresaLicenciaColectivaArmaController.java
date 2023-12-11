package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaLicenciaColectivaArmaService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaLicenciaColectivaArmaController {
    private final EmpresaLicenciaColectivaArmaService armaService;
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_ARMAS_URI = "/licencias/{licenciaUuid}/armas";

    @Autowired
    public EmpresaLicenciaColectivaArmaController(EmpresaLicenciaColectivaArmaService armaService, JwtUtils jwtUtils) {
        this.armaService = armaService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_ARMAS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaDto> obtenerArmas(
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) {
        return armaService.obtenerArmasPorLicenciaColectivaUuid(licenciaUuid);
    }

    @PostMapping(value = EMPRESA_ARMAS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto guardarArma(
            HttpServletRequest request,
            @RequestBody ArmaDto armaDto,
            @PathVariable(value = "licenciaUuid") String licenciaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaService.guardarArma(licenciaUuid, username, armaDto);
    }

    @PutMapping(value = EMPRESA_ARMAS_URI + "/{armaUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ArmaDto modificarArma(
            HttpServletRequest request,
            @RequestBody ArmaDto armaDto,
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            @PathVariable(value = "armaUuid") String armaUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaService.modificarArma(licenciaUuid, armaUuid, username, armaDto);
    }

    @PutMapping(value = EMPRESA_ARMAS_URI + "/{armaUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArmaDto eliminarArma(
            HttpServletRequest request,
            @PathVariable(value = "licenciaUuid") String licenciaUuid,
            @PathVariable(value = "armaUuid") String armaUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("arma") String arma
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return armaService.eliminarArma(licenciaUuid, armaUuid, username, new Gson().fromJson(arma, ArmaDto.class), archivo);
    }
}
