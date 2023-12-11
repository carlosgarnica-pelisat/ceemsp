package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "INCIDENCIAS_ARMAS")
@Getter
@Setter
public class IncidenciaArma extends CommonModel {
    @Column(name = "INCIDENCIA", nullable = false)
    private int incidencia;

    @Column(name = "ARMA", nullable = false)
    private int arma;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArmaStatusEnum status;

    @Column(name = "MOTIVO_ELIMINACION")
    private String motivoEliminacion;
}
