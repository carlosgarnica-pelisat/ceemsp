package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanRepository extends JpaRepository<Can, Integer> {
    List<Can> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Can> getAllByEmpresaAndEliminadoTrue(int empresa);
    List<Can> getAllByEmpresa(int empresa);
    List<Can> getAllByEmpresaAndStatus(int empresa, CanStatusEnum status);
    Can getByUuidAndEliminadoFalse(String uuid);
    Can getByUuid(String uuid);
}
