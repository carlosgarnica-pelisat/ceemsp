package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarContrasenaDto {
    private String actualPassword;
    private String password;
}
