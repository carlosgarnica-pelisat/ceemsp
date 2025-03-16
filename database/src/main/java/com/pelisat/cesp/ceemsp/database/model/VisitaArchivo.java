package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VISITAS_ARCHIVOS")
@Getter
@Setter
public class VisitaArchivo extends CommonModel {
    @Column(name = "VISITA", nullable = false)
    private int visita;

    @Column(name = "UBICACION_ARCHIVO", nullable = false)
    private String ubicacionArchivo;

    @Column(name = "DESCRIPCION")
    private String descripcion;
}
