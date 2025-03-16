package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ARMAS_DOMICILIOS")
@Getter
@Setter
public class ArmaDomicilio extends CommonModel {
    @Column(name = "ARMA", nullable = false)
    private int arma;

    @Column(name = "DOMICILIO_ANTERIOR", nullable = false)
    private int domicilioAnterior;

    @Column(name = "DOMICILIO_ACTUAL", nullable = false)
    private int domicilioActual;
}
