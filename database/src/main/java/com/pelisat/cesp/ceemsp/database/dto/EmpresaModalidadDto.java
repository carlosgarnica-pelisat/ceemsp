package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmpresaModalidadDto {
    private int id;
    private String uuid;
    private ModalidadDto modalidad;
    private SubmodalidadDto submodalidad;
    private String fechaInicio;
    private String fechaFin;
    private String numeroRegistroFederal;
}
