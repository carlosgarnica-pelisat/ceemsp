package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ClienteBajaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ClienteBajaViewRepository extends ReadOnlyRepository<ClienteBajaView, Long> {
    int countByFechaBajaIsLessThanEqual(LocalDateTime fechaCreacion);
}
