package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ACUERDOS")
public class Acuerdo extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "RUTA_ARCHIVO", nullable = false)
    private String rutaArchivo;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;

    @Column(name = "OBSERVACIONES_BAJA")
    private String observacionesBaja;

    @Column(name = "DOCUMENTO_FUNDATORIO_BAJA")
    private String documentoFundatorioBaja;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;
}
