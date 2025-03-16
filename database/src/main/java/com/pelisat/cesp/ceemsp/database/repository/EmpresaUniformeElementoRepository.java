package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaUniformeElemento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaUniformeElementoRepository extends JpaRepository<EmpresaUniformeElemento, Integer> {
    List<EmpresaUniformeElemento> findAllByUniformeAndEliminadoFalse(int uniforme);
    EmpresaUniformeElemento findByUuidAndEliminadoFalse(String uuid);
}
