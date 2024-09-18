package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ClienteAltaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ClienteAltaViewRepository extends ReadOnlyRepository<ClienteAltaView, Long> {
    int countByFechaCreacionIsLessThanEqual(LocalDateTime fechaCreacion);
}
