package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "MODALIDADES")
@Getter
@Setter
public class Modalidad extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "TIPO")
    @Enumerated(EnumType.STRING)
    private TipoTramiteEnum tipo;

    @Column(name = "SUBCATEGORIAS", nullable = false)
    private Boolean subcategorias;
}
