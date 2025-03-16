package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "EMPRESAS_EQUIPOS_MOVIMIENTOS")
public class EmpresaEquipoMovimiento extends CommonModel {
    @Column(name = "EMPRESA_EQUIPO", nullable = false)
    private int empresaEquipo;

    @Column(name = "ALTAS", nullable = false)
    private int altas;

    @Column(name = "BAJAS", nullable = false)
    private int bajas;

    @Column(name = "CANTIDAD_ACTUAL", nullable = false)
    private int cantidadActual;

    @Column(name = "OBSERVACIONES")
    private String observaciones;
}
