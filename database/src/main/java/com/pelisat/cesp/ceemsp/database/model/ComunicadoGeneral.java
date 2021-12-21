package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMUNICADOS_GENERALES")
@Getter
@Setter
public class ComunicadoGeneral extends CommonModel {
    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;

    @Column(name = "FECHA_PUBLICACION", nullable = false)
    private LocalDate fechaPublicacion;
}
