package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmpresaEscrituraSocioDto {
    private int id;
    private String uuid;
    private String nombres;
    private String apellidos;
    private SexoEnum sexo;
    private BigDecimal porcentajeAcciones;
    private String apellidoMaterno;
    private String curp;
    private boolean eliminado;
    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;
}
