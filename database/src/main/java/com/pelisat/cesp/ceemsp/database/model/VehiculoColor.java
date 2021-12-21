package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.VehiculoColorEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "VEHICULOS_COLORES")
@Getter
@Setter
public class VehiculoColor extends CommonModel {
    @Column(name = "VEHICULO", nullable = false)
    private int vehiculo;

    @Column(name = "COLOR", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehiculoColorEnum color;

    @Column(name = "DESCRIPCION")
    private String descripcion;
}
