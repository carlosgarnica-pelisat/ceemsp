package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Calle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalleRepository extends JpaRepository<Calle, Integer> {
    List<Calle> findAllByNombreContainsAndEliminadoFalse(String nombre);
    List<Calle> findAllByEliminadoFalse(Pageable pageRequest);
    List<Calle> findAllByEliminadoFalse();
}
