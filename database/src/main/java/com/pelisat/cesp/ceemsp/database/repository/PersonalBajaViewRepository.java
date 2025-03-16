package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalBajaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PersonalBajaViewRepository extends ReadOnlyRepository<PersonalBajaView, Long> {
    int countByFechaBajaIsLessThanEqual(LocalDateTime fechaCreacion);
}
