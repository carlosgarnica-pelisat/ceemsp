package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PersonalCertificacionDto {
    private String nombre;
    private String nombreInstructor;
    private BigDecimal duracion;
    private String fechaInicio;
    private String fechaFin;
    private String rutaArchivo;
}
