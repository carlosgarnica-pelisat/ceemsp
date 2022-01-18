package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "EMPRESAS_EQUIPOS")
public class EmpresaEquipo extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "EQUIPO", nullable = false)
    private int equipo;

    @Column(name = "CANTIDAD", nullable = false)
    private BigDecimal cantidad;
}
