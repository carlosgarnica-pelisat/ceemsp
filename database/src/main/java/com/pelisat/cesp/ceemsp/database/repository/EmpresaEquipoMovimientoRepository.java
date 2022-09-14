package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEquipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEquipoMovimientoRepository extends JpaRepository<EmpresaEquipoMovimiento, Integer> {
    List<EmpresaEquipoMovimiento> getAllByEmpresaEquipoAndEliminadoFalse(int empresaEquipo);
}
