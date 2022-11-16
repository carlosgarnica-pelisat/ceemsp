package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioTelefonoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaDomicilioTelefonoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmpresaDomicilioTelefonoController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_DOMICILIOS_URI = "/empresas/{empresaUuid}/domicilios/{domicilioUuid}/telefonos";
    private final EmpresaDomicilioTelefonoService empresaDomicilioTelefonoService;

    @Autowired
    public EmpresaDomicilioTelefonoController(JwtUtils jwtUtils, EmpresaDomicilioTelefonoService empresaDomicilioTelefonoService) {
        this.jwtUtils = jwtUtils;
        this.empresaDomicilioTelefonoService = empresaDomicilioTelefonoService;
    }

    @GetMapping(value = EMPRESA_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmpresaDomicilioTelefonoDto> obtenerTelefonosPorDomicilio(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid
    ) {
        return empresaDomicilioTelefonoService.obtenerTelefonosPorDomicilio(empresaUuid, domicilioUuid);
    }

    @PostMapping(value = EMPRESA_DOMICILIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioTelefonoDto crearTelefono(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid,
            @RequestBody EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaDomicilioTelefonoService.guardarTelefono(empresaUuid, domicilioUuid, username, empresaDomicilioTelefonoDto);
    }

    @PutMapping(value = EMPRESA_DOMICILIOS_URI + "/{telefonoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioTelefonoDto modificarTelefono(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid,
            @PathVariable(value = "telefonoUuid") String telefonoUuid,
            @RequestBody EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaDomicilioTelefonoService.modificarTelefono(empresaUuid, domicilioUuid, telefonoUuid, username, empresaDomicilioTelefonoDto);
    }

    @DeleteMapping(value = EMPRESA_DOMICILIOS_URI + "/{telefonoUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmpresaDomicilioTelefonoDto eliminarTelefono(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @PathVariable(value = "domicilioUuid") String domicilioUuid,
            @PathVariable(value = "telefonoUuid") String telefonoUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return empresaDomicilioTelefonoService.eliminarTelefono(empresaUuid, domicilioUuid, telefonoUuid, username);
    }
}
