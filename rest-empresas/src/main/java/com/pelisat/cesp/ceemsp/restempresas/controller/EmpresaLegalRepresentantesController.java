package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEscrituraRepresentanteService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaLegalRepresentantesController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_REPRESENTANTES_URI = "/escrituras/{escrituraUuid}/representantes";
    private final EmpresaEscrituraRepresentanteService empresaEscrituraRepresentanteService;

    @Autowired
    public EmpresaLegalRepresentantesController(EmpresaEscrituraRepresentanteService empresaEscrituraRepresentanteService,
                                                   JwtUtils jwtUtils) {
        this.empresaEscrituraRepresentanteService = empresaEscrituraRepresentanteService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_REPRESENTANTES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraRepresentanteDto> obtenerRepresentantesPorEscritura(
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraRepresentanteService.obtenerRepresentantesPorEscritura(escrituraUuid);
    }

    @PostMapping(value = EMPRESA_REPRESENTANTES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraRepresentanteDto crearEscrituraRepresentante(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraRepresentanteService.crearRepresentante(escrituraUuid, username, empresaEscrituraRepresentanteDto);
    }

    @PutMapping(value = EMPRESA_REPRESENTANTES_URI + "/{representanteUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraRepresentanteDto modificarEscrituraRepresentante(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "representanteUuid") String representanteUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraRepresentanteService.modificarRepresentante(escrituraUuid, representanteUuid, username, empresaEscrituraRepresentanteDto);
    }

    @DeleteMapping(value = EMPRESA_REPRESENTANTES_URI + "/{representanteUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraRepresentanteDto eliminarEscrituraRepresentante(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "representanteUuid") String representanteUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraRepresentanteService.eliminarRepresentante(escrituraUuid, representanteUuid, username);
    }
}
