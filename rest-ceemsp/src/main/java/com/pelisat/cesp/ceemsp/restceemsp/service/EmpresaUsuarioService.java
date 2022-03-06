package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;

import java.util.List;

public interface EmpresaUsuarioService {
    List<UsuarioDto> obtenerUsuarios(String uuid);
    UsuarioDto guardarUsuario(String uuid, String username, UsuarioDto usuarioDto);
    UsuarioDto modificarUsuario(String uuid, String usuarioUuid, String username, UsuarioDto usuarioDto);
    UsuarioDto eliminarUsuario(String uuid, String usuarioUuid, String username);
}
