package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaReporteMensual;
import com.pelisat.cesp.ceemsp.database.model.EmpresaReporteMensualMovimiento;
import com.pelisat.cesp.ceemsp.database.type.ReporteMovimientoEnum;
import com.pelisat.cesp.ceemsp.database.type.ReporteTipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaReporteMensualMovimientoRepository extends JpaRepository<EmpresaReporteMensualMovimiento, Integer> {
    EmpresaReporteMensualMovimiento getByReporteMensualAndTipoAndMovimientoAndEliminadoFalse(int reporteMensual, ReporteTipoEnum tipo, ReporteMovimientoEnum movimiento);
}
