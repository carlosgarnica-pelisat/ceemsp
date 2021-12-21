package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Can;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanRepository extends JpaRepository<Can, Integer> {
    List<Can> getAllByEmpresaAndEliminadoFalse(int empresa);

    Can getByUuidAndEliminadoFalse(String uuid);
}
