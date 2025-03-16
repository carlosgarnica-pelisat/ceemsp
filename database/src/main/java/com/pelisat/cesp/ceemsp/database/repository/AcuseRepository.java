package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Acuse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcuseRepository extends JpaRepository<Acuse, Integer> {
    Acuse findFirstByEliminadoFalseOrderByFechaCreacionDesc();
    Acuse findBySelloAndEliminadoFalse(String sello);
}
