package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VEHICULOS_SUBMARCAS")
@Getter
@Setter
public class VehiculoSubmarca extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    public VehiculoSubmarca() {
        super();
    }

    /*@Column(name = "CATEGORIA", nullable = false)
    private int categoria;*/
}
