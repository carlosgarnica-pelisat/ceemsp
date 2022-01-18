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
@Table(name = "EMPRESAS_UNIFORMES_ELEMENTOS")
public class EmpresaUniformeElemento extends CommonModel {
    @Column(name = "UNIFORME", nullable = false)
    private int uniforme;

    @Column(name = "ELEMENTO", nullable = false)
    private int elemento;

    @Column(name = "CANTIDAD")
    private BigDecimal cantidad;
}
