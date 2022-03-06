package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "LOCALIDADES")
public class Localidad extends CommonModel {
    @Column(name = "MUNICIPIO", nullable = false)
    private int municipio;

    @Column(name = "ESTADO", nullable = false)
    private int estado;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;
}
