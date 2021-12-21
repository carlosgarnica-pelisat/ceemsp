package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "INCIDENCIAS_CANES")
@Getter
@Setter
public class IncidenciaCan extends CommonModel {
    @Column(name = "INCIDENCIA", nullable = false)
    private int incidencia;

    @Column(name = "CAN", nullable = false)
    private int can;
}
