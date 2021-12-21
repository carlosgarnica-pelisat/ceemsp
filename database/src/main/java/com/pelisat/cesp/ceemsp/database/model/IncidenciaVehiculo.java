package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "INCIDENCIAS_VEHICULOS")
@Getter
@Setter
public class IncidenciaVehiculo extends CommonModel {
    @Column(name = "INCIDENCIA", nullable = false)
    private int incidencia;

    @Column(name = "VEHICULO", nullable = false)
    private int vehiculo;
}
