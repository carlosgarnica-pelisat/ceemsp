package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "MUNICIPIOS")
public class Municipio extends CommonModel {
    @Column(name = "ESTADO", nullable = false)
    private int estado;

    @Column(name = "CLAVE", nullable = false)
    private int clave;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;
}
