package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmpresaModalidadDto {
    private ModalidadDto modalidad;
    private SubmodalidadDto submodalidad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String numeroRegistroFederal;
}
