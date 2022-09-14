package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDto> getAllUsers();
    List<UsuarioDto> obtenerUsuariosInternos();
    UsuarioDto saveUser(UsuarioDto usuarioDto, String username);
    UsuarioDto getUserByUuid(String uuid);
    UsuarioDto getUserByUsername(String username);
    UsuarioDto getUserByEmail(String email);
    UsuarioDto getUserById(int id);
    UsuarioDto updateUserByUuid(String uuid, UsuarioDto usuarioDto, String username);
    UsuarioDto deleteUser(String uuid, String username);
}
