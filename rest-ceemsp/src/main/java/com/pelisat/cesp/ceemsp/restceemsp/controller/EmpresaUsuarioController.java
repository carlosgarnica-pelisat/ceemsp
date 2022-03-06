package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaUsuarioService;
import com.pelisat.cesp.ceemsp.restceemsp.service.EmpresaVehiculoService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class EmpresaUsuarioController {
    private final JwtUtils jwtUtils;
    private static final String EMPRESA_USUARIOS_URI = "/empresas/{empresaUuid}/usuarios";
    private final EmpresaUsuarioService empresaUsuarioService;

    @Autowired
    public EmpresaUsuarioController(
            JwtUtils jwtUtils,
            EmpresaUsuarioService empresaUsuarioService
    ) {
        this.jwtUtils = jwtUtils;
        this.empresaUsuarioService = empresaUsuarioService;
    }

    @GetMapping(value = EMPRESA_USUARIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto obtenerUsuarios(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody UsuarioDto usuarioDto,
            HttpServletRequest httpServletRequest
    ) {
        return null;
    }

    @PostMapping(value = EMPRESA_USUARIOS_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto guardarUsuario(
            @PathVariable(value = "empresaUuid") String empresaUuid,
            @RequestBody UsuarioDto usuarioDto,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return empresaUsuarioService.guardarUsuario(empresaUuid, username, usuarioDto);
    }
}
