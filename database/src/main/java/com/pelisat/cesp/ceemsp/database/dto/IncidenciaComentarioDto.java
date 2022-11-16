package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidenciaComentarioDto {
    private int id;
    private String uuid;
    private UsuarioDto usuario;
    private String fecha;
    private String comentario;
}
