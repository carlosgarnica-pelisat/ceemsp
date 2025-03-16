package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ArmaAltaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ArmaAltaViewRepository extends ReadOnlyRepository<ArmaAltaView, Long> {
    int countByFechaCreacionIsLessThanEqual(LocalDateTime fechaCreacion);
}
