package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.TipoInfraestructura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoInfraestructuraRepository extends JpaRepository<TipoInfraestructura, Integer> {
    List<TipoInfraestructura> getAllByEliminadoFalse();

    TipoInfraestructura getByUuidAndEliminadoFalse(String uuid);
}
