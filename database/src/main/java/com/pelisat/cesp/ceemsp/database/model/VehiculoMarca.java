package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "VEHICULOS_MARCAS")
public class VehiculoMarca extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehiculoTipoEnum tipo;

    public VehiculoMarca() {
        super();
    }
}
