package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEscrituraRepository extends JpaRepository<EmpresaEscritura, Integer> {
    List<EmpresaEscritura> findAllByEmpresaAndEliminadoFalse(int empresa);
}
