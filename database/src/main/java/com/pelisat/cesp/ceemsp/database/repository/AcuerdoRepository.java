package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Acuerdo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcuerdoRepository extends JpaRepository<Acuerdo, Integer> {
    List<Acuerdo> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Acuerdo> getAllByEmpresaAndEliminadoTrue(int empresa);
    Acuerdo getByUuid(String uuid);
}
