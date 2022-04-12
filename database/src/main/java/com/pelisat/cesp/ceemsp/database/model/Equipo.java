package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "EQUIPOS")
@Getter
@Setter
public class Equipo extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FORMA_EJECUCION")
    @Enumerated(EnumType.STRING)
    private FormaEjecucionEnum formaEjecucion;

    public Equipo() {
        super();
    }
}
