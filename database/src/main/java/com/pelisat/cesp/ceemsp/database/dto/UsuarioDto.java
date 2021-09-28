package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.RolTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDto {
    private Integer id;
    private String uuid;
    private String username;
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
    private EmpresaDto empresa;
    private RolTypeEnum rol;
}
