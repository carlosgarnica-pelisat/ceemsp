package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuzonInternoDestinatarioDto {
    private String tipoDestinatario;
    private String email;
    private UsuarioDto usuario;
    private EmpresaDto empresa;
}
