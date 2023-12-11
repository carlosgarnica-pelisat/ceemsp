package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "PERSONAL_ARMAS")
public class PersonalArma extends CommonModel {
    @Column(name = "PERSONAL", nullable = false)
    private int personal;

    @Column(name = "ARMA", nullable = false)
    private int arma;

    @Column(name = "TIPO")
    @Enumerated(EnumType.STRING)
    private ArmaTipoEnum tipo;

    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "MOTIVO_BAJA_ASIGNACION")
    private String motivoBajaAsignacion;
}
