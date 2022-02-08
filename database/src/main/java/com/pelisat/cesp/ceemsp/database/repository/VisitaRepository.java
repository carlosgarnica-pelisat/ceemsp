package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Integer> {
    List<Visita> getAllByEliminadoFalse();
    Visita findByUuidAndEliminadoFalse(String uuid);
}
