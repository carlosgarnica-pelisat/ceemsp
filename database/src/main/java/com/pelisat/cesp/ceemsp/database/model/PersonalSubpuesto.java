package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "PERSONAL_SUBPUESTOS")
public class PersonalSubpuesto extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "PUESTO")
    private int puesto;

    @Column(name = "PORTACION")
    private boolean portacion;

    @Column(name = "CUIP")
    private boolean cuip;
}
