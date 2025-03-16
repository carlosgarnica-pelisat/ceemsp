package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "EMPRESAS_UNIFORMES_ELEMENTOS_MOVIMIENTOS")
public class EmpresaUniformeElementoMovimiento extends CommonModel {
    @Column(name = "UNIFORME_ELEMENTO", nullable = false)
    private int uniformeElemento;

    @Column(name = "ALTAS", nullable = false)
    private int altas;

    @Column(name = "BAJAS", nullable = false)
    private int bajas;

    @Column(name = "CANTIDAD_ACTUAL", nullable = false)
    private int cantidadActual;
}
