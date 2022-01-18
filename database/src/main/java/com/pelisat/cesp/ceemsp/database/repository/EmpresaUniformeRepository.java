package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaUniforme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaUniformeRepository extends JpaRepository<EmpresaUniforme, Integer> {
    List<EmpresaUniforme> findAllByEmpresaAndEliminadoFalse(int empresa);
    EmpresaUniforme findByUuidAndEliminadoFalse(String uuid);
}
