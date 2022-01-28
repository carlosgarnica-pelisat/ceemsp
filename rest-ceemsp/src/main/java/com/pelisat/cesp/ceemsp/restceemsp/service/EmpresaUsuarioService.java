package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;

public interface EmpresaUsuarioService {
    UsuarioDto guardarUsuario(String uuid, String username, UsuarioDto usuarioDto);
}
