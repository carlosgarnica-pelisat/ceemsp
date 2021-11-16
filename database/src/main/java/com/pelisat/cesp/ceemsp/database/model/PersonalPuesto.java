package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "PERSONAL_PUESTOS")
public class PersonalPuesto extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "REQUIERE_MODALIDAD")
    private boolean requiereModalidad;

    @Column(name = "REQUIERE_CUIP")
    private boolean requiereCuip;

    @Column(name = "TIENE_PORTACION")
    private boolean tienePortacion;
}
