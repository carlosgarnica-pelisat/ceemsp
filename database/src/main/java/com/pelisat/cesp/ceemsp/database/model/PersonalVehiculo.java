package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "PERSONAL_VEHICULOS")
public class PersonalVehiculo extends CommonModel {
    @Column(name = "PERSONAL", nullable = false)
    private int personal;

    @Column(name = "VEHICULO", nullable = false)
    private int vehiculo;

    @Column(name = "OBSERVACIONES")
    private String observaciones;
}
