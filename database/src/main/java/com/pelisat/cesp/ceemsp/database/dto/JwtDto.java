package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.RolTypeEnum;

public class JwtDto {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwtToken;
    private UsuarioDto usuario;

    public JwtDto(String jwttoken) {
        this.jwtToken = jwttoken;
    }

    public JwtDto(String jwtToken, UsuarioDto usuario) {
        this.jwtToken = jwtToken;
        this.usuario = usuario;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }
}
