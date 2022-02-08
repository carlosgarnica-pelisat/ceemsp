package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.CanConstanciaSaludDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.CanConstanciaSaludService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CanConstanciaSaludController {
    private final CanConstanciaSaludService canConstanciaSaludService;
    private final JwtUtils jwtUtils;
    private static final String CAN_CONSTANCIA_URI = "/empresas/{empresaUuid}/canes/{canUuid}/constancias";

    @Autowired
    public CanConstanciaSaludController(CanConstanciaSaludService canConstanciaSaludService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.canConstanciaSaludService = canConstanciaSaludService;
    }

    @GetMapping(value = CAN_CONSTANCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanConstanciaSaludDto> obtenerCanesConstanciasSaludPorCanUuid(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid
    ) {
        return canConstanciaSaludService.obtenerConstanciasSaludPorCanUuid(empresaUuid, canUuid);
    }

    @PostMapping(value = CAN_CONSTANCIA_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CanConstanciaSaludDto guardarCanConstanciasalud(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "canUuid") String canUuid,
            @RequestBody CanConstanciaSaludDto canConstanciaSaludDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return canConstanciaSaludService.guardarConstanciaSalud(empresaUuid, canUuid, username, canConstanciaSaludDto);
    }

}
