package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidenciaComentarioDto {
    private UsuarioDto usuario;
    private String fecha;
    private String comentario;
}
