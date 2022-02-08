package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.IncidenciaPersona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaPersonaRepository extends JpaRepository<IncidenciaPersona, Integer> {
    List<IncidenciaPersona> getAllByIncidenciaAndEliminadoFalse(int incidenciaId);
}
