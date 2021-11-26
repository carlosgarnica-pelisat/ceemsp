package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraConsejoDto;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraConsejoRepository;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraApoderadoService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaEscrituraConsejoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaEscrituraConsejoAdministracionController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_CONSEJO_URI = "/empresas/{empresaUuid}/escrituras/{escrituraUuid}/consejos";
    private final EmpresaEscrituraConsejoService empresaEscrituraConsejoService;

    @Autowired
    public EmpresaEscrituraConsejoAdministracionController(EmpresaEscrituraConsejoService empresaEscrituraConsejoService,
                                               JwtUtils jwtUtils) {
        this.empresaEscrituraConsejoService = empresaEscrituraConsejoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_CONSEJO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraConsejoDto> obtenerConsejosPorEscritura(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraConsejoService.obtenerConsejosPorEscritura(empresaUuid, escrituraUuid);
    }

    @GetMapping(value = EMPRESA_CONSEJO_URI + "/{consejoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraApoderadoDto obtenerEscrituraConsejoPorUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "apoderadoUuid") String socioUuid
    ) {
        return null;
        //return empresaEscrituraApoderadoService.obtenerRepresentantePorUuid(empresaUuid, escrituraUuid, socioUuid, false);
    }

    @PostMapping(value = EMPRESA_CONSEJO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraConsejoDto crearEscrituraConsejo(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraConsejoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraConsejoService.crearConsejo(empresaUuid, escrituraUuid, username, empresaEscrituraApoderadoDto);
    }
}
