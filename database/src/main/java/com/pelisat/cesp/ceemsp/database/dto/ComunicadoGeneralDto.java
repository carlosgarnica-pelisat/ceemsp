package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComunicadoGeneralDto {
    private int id;
    private String uuid;
    private String titulo;
    private String descripcion;
    private String fechaPublicacion;
}
