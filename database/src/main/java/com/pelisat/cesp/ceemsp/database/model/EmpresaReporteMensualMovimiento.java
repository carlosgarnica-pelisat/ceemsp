package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.ReporteMovimientoEnum;
import com.pelisat.cesp.ceemsp.database.type.ReporteTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "EMPRESAS_REPORTES_MENSUALES_MOVIMIENTOS")
@Getter
@Setter
public class EmpresaReporteMensualMovimiento extends CommonModel {
    @Column(name = "REPORTE_MENSUAL", nullable = false)
    private int reporteMensual;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReporteTipoEnum tipo;

    @Column(name = "MOVIMIENTO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReporteMovimientoEnum movimiento;

    @Column(name = "ELEMENTOS")
    private String elementos;
}
