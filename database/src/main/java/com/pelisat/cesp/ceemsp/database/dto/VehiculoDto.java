package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.dto.metadata.VehiculoFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.type.NivelBlindajeEnum;
import com.pelisat.cesp.ceemsp.database.type.VehiculoOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.VehiculoStatusEnum;
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
    private VehiculoUsoDto uso;
    private EmpresaDomicilioDto domicilio;
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
    private String razonSocial;
    private String fechaInicio;
    private String fechaFin;
    private boolean eliminado;
    private String fechaCreacion;
    private String fechaActualizacion;
    private VehiculoStatusEnum status;
    private PersonaDto personalAsignado;

    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;

    private List<VehiculoColorDto> colores;
    private List<VehiculoFotografiaMetadata> fotografias;

    private boolean coloresCapturado;
    private boolean fotografiaCapturada;
    private boolean constanciaBlindajeCargada;
    private Boolean eliminadoIncidencia;
    private String fechaCreacionIncidencia;
    private String fechaEliminacionIncidencia;
    private EmpresaDto empresa;
}
