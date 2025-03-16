package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SUBMODALIDADES")
@Getter
@Setter
public class Submodalidad extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "CATEGORIA", nullable = false)
    private int categoria;

    @Column(name = "CANES")
    private Boolean canes;

    @Column(name = "ARMAS")
    private Boolean armas;
}
