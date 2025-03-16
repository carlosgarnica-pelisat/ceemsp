package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.AcuerdoTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
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

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private AcuerdoTipoEnum tipo;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "MULTA_UMAS")
    private BigDecimal multaUmas;

    @Column(name = "MULTA_PESOS")
    private BigDecimal multaPesos;

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
