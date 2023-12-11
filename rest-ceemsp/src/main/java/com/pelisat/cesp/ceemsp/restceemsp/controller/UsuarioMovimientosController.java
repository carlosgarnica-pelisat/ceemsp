package com.pelisat.cesp.ceemsp.restceemsp.controller;

import com.pelisat.cesp.ceemsp.database.dto.MovimientoDto;
import com.pelisat.cesp.ceemsp.database.type.TipoMovimientoUsuarioEnum;
import com.pelisat.cesp.ceemsp.restceemsp.service.UsuarioMovimientosService;
import com.pelisat.cesp.ceemsp.restceemsp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsuarioMovimientosController {
    private final JwtUtils jwtUtils;
    private final UsuarioMovimientosService usuarioMovimientosService;
    private static final String USUARIO_URI = "/usuarios/{usuarioUuid}";

    @Autowired
    public UsuarioMovimientosController(JwtUtils jwtUtils, UsuarioMovimientosService usuarioMovimientosService) {
        this.jwtUtils = jwtUtils;
        this.usuarioMovimientosService = usuarioMovimientosService;
    }

    @GetMapping(value = USUARIO_URI + "/movimientos/{tipoMovimiento}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MovimientoDto> obtenerMovimientosPorUsuario(
            @PathVariable(value = "usuarioUuid") String usuarioUuid,
            @PathVariable(value = "tipoMovimiento")TipoMovimientoUsuarioEnum tipoMovimientoUsuarioEnum
            ) {
        return usuarioMovimientosService.obtenerMovimientos(usuarioUuid, tipoMovimientoUsuarioEnum);
    }
}
