package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentanaDto {
    private int id;
    private String uuid;
    private String nombre;
    private String fechaInicio;
    private String fechaFin;
}
