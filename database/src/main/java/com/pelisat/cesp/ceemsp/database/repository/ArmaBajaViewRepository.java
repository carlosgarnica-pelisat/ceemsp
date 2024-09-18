package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ArmaBajaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ArmaBajaViewRepository extends ReadOnlyRepository<ArmaBajaView, Long> {
    int countByFechaBajaIsLessThanEqual(LocalDateTime fechaCreacion);
}
