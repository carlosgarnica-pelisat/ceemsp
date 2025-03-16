package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalAltaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PersonalAltaViewRepository extends ReadOnlyRepository<PersonalAltaView, Long> {
    int countByFechaCreacionIsLessThanEqual(LocalDateTime fechaCreacion);
    int countByFechaCreacionIsGreaterThanEqualAndFechaCreacionIsLessThanEqual(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
