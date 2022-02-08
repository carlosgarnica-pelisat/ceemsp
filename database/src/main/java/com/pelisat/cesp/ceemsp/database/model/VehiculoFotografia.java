package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VEHICULOS_FOTOGRAFIAS")
@Getter
@Setter
public class VehiculoFotografia extends CommonModel {
    @Column(name = "VEHICULO", nullable = false)
    private int vehiculo;

    @Column(name = "UBICACION_ARCHIVO", nullable = false)
    private String ubicacionArchivo;

    @Column(name = "DESCRIPCION")
    private String descripcion;
}
