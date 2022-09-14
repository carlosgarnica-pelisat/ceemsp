package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.BuzonInterno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuzonInternoRepository extends JpaRepository<BuzonInterno, Integer> {
    BuzonInterno getByUuidAndEliminadoFalse(String uuid);
}
