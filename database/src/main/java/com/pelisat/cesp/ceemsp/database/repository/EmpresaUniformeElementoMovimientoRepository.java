package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaUniformeElementoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaUniformeElementoMovimientoRepository extends JpaRepository<EmpresaUniformeElementoMovimiento, Integer> {
    List<EmpresaUniformeElementoMovimiento> getAllByUniformeElementoAndEliminadoFalse(int uniformeElemento);
}
