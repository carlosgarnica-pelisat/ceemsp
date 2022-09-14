package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaEscrituraApoderadoDto {
    private int id;
    private String uuid;
    private String nombres;
    private String apellidos;
    private String apellidoMaterno;
    private SexoEnum sexo;
    private String fechaInicio;
    private String fechaFin;
    private String curp;
    private boolean eliminado;
    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;
}
