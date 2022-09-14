package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.TipoBuzonInternoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "BUZON_INTERNO")
public class BuzonInterno extends CommonModel {
    @Column(name = "MOTIVO", nullable = false)
    private String motivo;

    @Column(name = "MENSAJE", nullable = false)
    private String mensaje;
}
