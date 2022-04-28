package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.restceemsp.service.UsuarioService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;
    private static final String USUARIO_URI = "/usuarios";

    @Autowired
    public UsuarioController(UsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = USUARIO_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UsuarioDto> obtenerUsuarios() {
        return usuarioService.getAllUsers();
    }

    @GetMapping(value = USUARIO_URI + "/perfil", produces = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto obtenerPerfil(
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(httpServletRequest.getHeader("Authorization"));
        return usuarioService.getUserByEmail(username);
    }

    @GetMapping(value = USUARIO_URI + "/{usuarioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto obtenerUsuarioPorUuid(
            @PathVariable(value = "usuarioUuid") String usuarioUuid
    ) {
        return usuarioService.getUserByUuid(usuarioUuid);
    }

    @PostMapping(value = USUARIO_URI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto guardarUsuario(
            HttpServletRequest request,
            @RequestBody UsuarioDto usuarioDto
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return usuarioService.saveUser(usuarioDto, username);
    }

    @PutMapping(value = USUARIO_URI + "/{usuarioUuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto modificarUsuario(
            @PathVariable(value = "usuarioUuid") String usuarioUuid,
            @RequestBody UsuarioDto usuarioDto,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return usuarioService.updateUserByUuid(usuarioUuid, usuarioDto, username);
    }

    @DeleteMapping(value = USUARIO_URI + "/{usuarioUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioDto eliminarUsuario(
            @PathVariable(value = "usuarioUuid") String usuarioUuid,
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return usuarioService.deleteUser(usuarioUuid, username);
    }
}
