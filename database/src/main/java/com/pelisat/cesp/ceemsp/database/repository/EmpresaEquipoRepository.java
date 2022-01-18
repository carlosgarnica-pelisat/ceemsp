package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEquipoRepository extends JpaRepository<EmpresaEquipo, Integer> {
    List<EmpresaEquipo> findAllByEmpresaAndEliminadoFalse(int empresa);
    EmpresaEquipo findByUuidAndEliminadoFalse(String uuid);
}
