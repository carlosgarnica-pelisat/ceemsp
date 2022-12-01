package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.ComunicadoGeneralDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.ComunicadoGeneralService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ComunicadoGeneralController {
    private final JwtUtils jwtUtils;
    private static final String COMUNICADOS_GENERALES_URI = "/comunicados-generales";
    private final ComunicadoGeneralService comunicadoGeneralService;

    @Autowired
    public ComunicadoGeneralController(JwtUtils jwtUtils, ComunicadoGeneralService comunicadoGeneralService) {
        this.comunicadoGeneralService = comunicadoGeneralService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = COMUNICADOS_GENERALES_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComunicadoGeneralDto> obtenerComunicadosGenerales() {
        return comunicadoGeneralService.obtenerComunicadosGenerales(null, null, null);
    }

    @GetMapping(value = COMUNICADOS_GENERALES_URI + "/ultimo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto obtenerUltimoComunicado() {
        return comunicadoGeneralService.obtenerUltimoComunicado();
    }

    @GetMapping(value = COMUNICADOS_GENERALES_URI + "/{comunicadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto obtenerComunicadoPorUuid(
            @PathVariable(value = "comunicadoUuid") String comunicadoUuid
    ) {
        return comunicadoGeneralService.obtenerComunicadoPorUuid(comunicadoUuid);
    }

    @PostMapping(value = COMUNICADOS_GENERALES_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto guardarComunicado(
            @RequestBody ComunicadoGeneralDto comunicadoGeneralDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return comunicadoGeneralService.guardarComunicado(username, comunicadoGeneralDto);
    }

    @PutMapping(value = COMUNICADOS_GENERALES_URI + "/{comunicadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto modificarComunicado(
            @RequestBody ComunicadoGeneralDto comunicadoGeneralDto,
            HttpServletRequest request,
            @PathVariable(value = "comunicadoUuid") String comunicadoUuid
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return comunicadoGeneralService.modificarComunicado(comunicadoUuid, username, comunicadoGeneralDto);
    }

    @DeleteMapping(value = COMUNICADOS_GENERALES_URI + "/{comunicadoUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ComunicadoGeneralDto eliminarComunicado(
            HttpServletRequest request,
            @PathVariable(value = "comunicadoUuid") String comunicadoUuid
    ) throws Exception {
        String username =  jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return comunicadoGeneralService.eliminarComunicado(comunicadoUuid, username);
    }
}
