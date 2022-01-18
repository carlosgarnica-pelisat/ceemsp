package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Integer> {
    List<Equipo> findAllByEliminadoFalse();

    Equipo findByUuidAndEliminadoFalse(String uuid);
}
