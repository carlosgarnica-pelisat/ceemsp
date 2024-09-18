package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoAltaView;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VehiculoAltaViewRepository extends ReadOnlyRepository<VehiculoAltaView, Long> {
    int countByFechaCreacionIsLessThanEqual(LocalDateTime fechaCreacion);
}
