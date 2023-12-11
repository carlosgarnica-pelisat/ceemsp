package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.IncidenciaCan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaCanRepository extends JpaRepository<IncidenciaCan, Integer> {
    List<IncidenciaCan> getAllByIncidenciaAndEliminadoFalse(int incidenciaId);
    IncidenciaCan getByIncidenciaAndCanAndEliminadoFalse(int incidencia, int can);
    int countByIncidenciaAndEliminadoFalse(int incidencia);
}
