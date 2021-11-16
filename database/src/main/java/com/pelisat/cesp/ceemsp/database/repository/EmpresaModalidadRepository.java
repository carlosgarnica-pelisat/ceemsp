package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaModalidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaModalidadRepository extends JpaRepository<EmpresaModalidad, Integer> {
    List<EmpresaModalidad> findAllByEmpresaAndEliminadoFalse(int empresa);

    EmpresaModalidad findByUuidAndEliminadoFalse(String uuid);
}
