package com.pelisat.cesp.ceemsp.database.model;


import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "VISITAS")
@Getter
@Setter
public class Visita extends CommonModel {
    @Column(name = "EMPRESA")
    private Integer empresa;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoVisitaEnum tipo;

    @Column(name = "NUMERO_REGISTRO")
    private String numeroRegistro;

    @Column(name = "NUMERO_ORDEN", nullable = false)
    private String numeroOrden;

    @Column(name = "FECHA_VISITA", nullable = false)
    private LocalDate fechaVisita;

    @Column(name = "REQUERIMIENTO", nullable = false)
    private boolean requerimiento;

    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "FECHA_TERMINO")
    private LocalDate fechaTermino;

    @Column(name = "RESPONSABLE", nullable = false)
    private int responsable;

    @Column(name = "DOMICILIO_1")
    private String domicilio1;

    @Column(name = "NUMERO_EXTERIOR")
    private String numeroExterior;

    @Column(name = "NUMERO_INTERIOR")
    private String numeroInterior;

    @Column(name = "DOMICILIO_2")
    private String domicilio2;

    @Column(name = "DOMICILIO_3")
    private String domicilio3;

    @Column(name = "DOMICILIO_4")
    private String domicilio4;

    @Column(name = "CODIGO_POSTAL")
    private String codigoPostal;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "PAIS")
    private String pais;
}
