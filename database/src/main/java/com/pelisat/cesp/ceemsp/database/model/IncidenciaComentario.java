package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "INCIDENCIAS_COMENTARIOS")
@Getter
@Setter
public class IncidenciaComentario extends CommonModel {
    @Column(name = "INCIDENCIA", nullable = false)
    private int incidencia;

    @Column(name = "COMENTARIO", nullable = false)
    private String comentario;
}
