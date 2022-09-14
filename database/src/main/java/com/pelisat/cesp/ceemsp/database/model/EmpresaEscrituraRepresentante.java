package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS_ESCRITURAS_REPRESENTANTES")
@Getter
@Setter
public class EmpresaEscrituraRepresentante extends CommonModel {
    @Column(name = "ESCRITURA", nullable = false)
    private int escritura;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false)
    private String apellidos;

    @Column(name = "APELLIDO_MATERNO")
    private String apellidoMaterno;

    @Column(name = "SEXO", nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

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
