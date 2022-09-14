package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS_ESCRITURAS_SOCIOS")
@Getter
@Setter
public class EmpresaEscrituraSocio extends CommonModel {
    @Column(name = "ESCRITURA", nullable = false)
    private int escritura;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false)
    private String apellidos;

    @Column(name = "SEXO", nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

    @Column(name = "PORCENTAJE_ACCIONES", nullable = false)
    private BigDecimal porcentajeAcciones;

    @Column(name = "APELLIDO_MATERNO")
    private String apellidoMaterno;

    @Column(name = "CURP")
    private String curp;

    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;

    @Column(name = "OBSERVACIONES_BAJA")
    private String observacionesBaja;

    @Column(name = "DOCUMENTO_FUNDATORIO_BAJA")
    private String documentoFundatorioBaja;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;
}