package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoBajaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VehiculoBajaViewRepository extends ReadOnlyRepository<VehiculoBajaView, Long> {
    int countByFechaBajaIsLessThanEqual(LocalDateTime fechaCreacion);
}
