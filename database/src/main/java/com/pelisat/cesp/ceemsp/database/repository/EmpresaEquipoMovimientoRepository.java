package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEquipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmpresaEquipoMovimientoRepository extends JpaRepository<EmpresaEquipoMovimiento, Integer> {
    List<EmpresaEquipoMovimiento> getAllByEmpresaEquipoAndEliminadoFalse(int empresaEquipo);
    List<EmpresaEquipoMovimiento> findAllByEmpresaEquipoAndFechaCreacionLessThanAndEliminadoFalse(int empresaEquipo, LocalDateTime fechaFin);
    List<EmpresaEquipoMovimiento> findAllByEmpresaEquipoAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresaEquipo, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    List<EmpresaEquipoMovimiento> findAllByEmpresaEquipoAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresaEquipo, LocalDateTime fechaFin, LocalDateTime fechaInicio);
}
