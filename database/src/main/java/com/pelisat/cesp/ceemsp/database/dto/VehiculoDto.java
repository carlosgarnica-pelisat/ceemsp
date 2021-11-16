package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.NivelBlindajeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VehiculoDto {
    private VehiculoTipoDto tipo;
    private VehiculoMarcaDto marca;
    private VehiculoSubmarcaDto submarca;
    private String anio;
    private String color;
    private boolean rotulado;
    private String placas;
    private String serie;
    private String origen;
    private boolean blindado;
    private String serieBlindaje;
    private String fechaBlindaje;
    private String numeroHolograma;
    private String placaMetalica;
    private String empresaBlindaje;
    private NivelBlindajeEnum nivelBlindaje;
}
