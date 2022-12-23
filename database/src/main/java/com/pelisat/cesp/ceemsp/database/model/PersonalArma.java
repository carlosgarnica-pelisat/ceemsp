package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "PERSONAL_ARMAS")
public class PersonalArma extends CommonModel {
    @Column(name = "PERSONAL", nullable = false)
    private int personal;

    @Column(name = "ARMA", nullable = false)
    private int arma;

    @Column(name = "OBSERVACIONES")
    private String observaciones;
}
