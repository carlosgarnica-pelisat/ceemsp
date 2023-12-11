package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraConsejoDto;
import com.pelisat.cesp.ceemsp.restempresas.service.EmpresaEscrituraConsejoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaLegalConsejoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_CONSEJO_URI = "/escrituras/{escrituraUuid}/consejos";
    private final EmpresaEscrituraConsejoService empresaEscrituraConsejoService;

    @Autowired
    public EmpresaLegalConsejoController(EmpresaEscrituraConsejoService empresaEscrituraConsejoService,
                                                           JwtUtils jwtUtils) {
        this.empresaEscrituraConsejoService = empresaEscrituraConsejoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = EMPRESA_CONSEJO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaEscrituraConsejoDto> obtenerConsejosPorEscritura(
            @PathVariable(value = "escrituraUuid") String escrituraUuid
    ) {
        return empresaEscrituraConsejoService.obtenerConsejosPorEscritura(escrituraUuid);
    }

    @PostMapping(value = EMPRESA_CONSEJO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraConsejoDto crearEscrituraConsejo(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraConsejoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraConsejoService.crearConsejo(escrituraUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_CONSEJO_URI + "/{consejoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaEscrituraConsejoDto modificarEscrituraConsejo(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "consejoUuid") String consejoUuid,
            HttpServletRequest request,
            @RequestBody EmpresaEscrituraConsejoDto empresaEscrituraApoderadoDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraConsejoService.actualizarConsejo(escrituraUuid, consejoUuid, username, empresaEscrituraApoderadoDto);
    }

    @PutMapping(value = EMPRESA_CONSEJO_URI + "/{consejoUuid}/borrar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaEscrituraConsejoDto eliminarEscrituraConsejo(
            @PathVariable(value = "escrituraUuid") String escrituraUuid,
            @PathVariable(value = "consejoUuid") String consejoUuid,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("consejo") String consejo,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaEscrituraConsejoService.eliminarConsejo(escrituraUuid, consejoUuid, username, new Gson().fromJson(consejo, EmpresaEscrituraConsejoDto.class), archivo);
    }
}
