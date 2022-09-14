package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ARMAS")
public class Arma extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "LICENCIA_COLECTIVA", nullable = false)
    private int licenciaColectiva;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArmaTipoEnum tipo;

    @Column(name = "CLASE", nullable = false)
    private int clase;

    @Column(name = "MARCA", nullable = false)
    private int marca;

    @Column(name = "CALIBRE", nullable = false)
    private String calibre;

    @Column(name = "BUNKER", nullable = false)
    private int bunker;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArmaStatusEnum status;

    @Column(name = "SERIE", nullable = false)
    private String serie;

    @Column(name = "MATRICULA", nullable = false)
    private String matricula;

    @Column(name = "PERSONAL")
    private Integer personal;

    @Column(name = "INCIDENCIA")
    private Integer incidencia;

    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;

    @Column(name = "OBSERVACIONES_BAJA")
    private String observacionesBaja;

    @Column(name = "DOCUMENTO_FUNDATORIO_BAJA")
    private String documentoFundatorioBaja;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;
}
