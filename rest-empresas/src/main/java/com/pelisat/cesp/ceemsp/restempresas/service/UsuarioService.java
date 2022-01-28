package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;

import java.util.List;

public interface UsuarioService {
    UsuarioDto getUserByUsername(String username);

    UsuarioDto getUserByEmail(String email);

    UsuarioDto getUserById(int id);
}
