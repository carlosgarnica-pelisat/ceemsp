package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExisteUsuarioDto {
    private Boolean existe;
    private String email;
    private String username;
    private UsuarioDto usuario;
}
