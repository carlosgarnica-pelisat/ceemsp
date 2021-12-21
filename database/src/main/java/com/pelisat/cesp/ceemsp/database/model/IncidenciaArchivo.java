package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "INCIDENCIAS_ARCHIVOS")
@Getter
@Setter
public class IncidenciaArchivo extends CommonModel {
    @Column(name = "INCIDENCIA", nullable = false)
    private int incidencia;

    @Column(name = "RUTA_ARCHIVO", nullable = false)
    private String rutaArchivo;
}
