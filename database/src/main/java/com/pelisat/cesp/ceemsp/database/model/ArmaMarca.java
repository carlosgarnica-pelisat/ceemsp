package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "ARMAS_MARCAS")
public class ArmaMarca extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    public ArmaMarca() {
        super();
    }
}
