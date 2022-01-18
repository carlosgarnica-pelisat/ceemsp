package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.NivelBlindajeEnum;
import com.pelisat.cesp.ceemsp.database.type.VehiculoOrigenEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class VehiculoDto {
    private int id;
    private String uuid;
    private VehiculoTipoDto tipo;
    private VehiculoMarcaDto marca;
    private VehiculoSubmarcaDto submarca;
    private String anio;
    private String color;
    private boolean rotulado;
    private String placas;
    private String serie;
    private VehiculoOrigenEnum origen;
    private boolean blindado;
    private String serieBlindaje;
    private String fechaBlindaje;
    private String numeroHolograma;
    private String placaMetalica;
    private String empresaBlindaje;
    private NivelBlindajeEnum nivelBlindaje;

    private List<VehiculoColorDto> colores;
}
