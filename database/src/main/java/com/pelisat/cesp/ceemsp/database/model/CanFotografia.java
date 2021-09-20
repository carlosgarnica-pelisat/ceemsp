package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CANES_FOTOGRAFIAS")
@Getter
@Setter
public class CanFotografia extends CommonModel {
    //TODO: Convertir a relationship de hibernate
    @Column(name = "CAN", nullable = false)
    private Integer can;

    @Column(name = "RUTA", nullable = false)
    private String ruta;

    @Column(name = "DESCRIPCION")
    private String descripcion;
}
