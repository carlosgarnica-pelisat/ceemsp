package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaFormaEjecucion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaFormaEjecucionRepository extends JpaRepository<EmpresaFormaEjecucion, Integer> {
    List<EmpresaFormaEjecucion> getAllByEmpresaAndEliminadoFalse(int empresa);
}
