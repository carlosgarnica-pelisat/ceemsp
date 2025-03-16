package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VEHICULOS_DOMICILIOS")
@Getter
@Setter
public class VehiculoDomicilio extends CommonModel {
    @Column(name = "VEHICULOS", nullable = false)
    private int vehiculo;

    @Column(name = "DOMICILIO_ANTERIOR", nullable = false)
    private int domicilioAnterior;

    @Column(name = "DOMICILIO_ACTUAL", nullable = false)
    private int domicilioActual;
}
