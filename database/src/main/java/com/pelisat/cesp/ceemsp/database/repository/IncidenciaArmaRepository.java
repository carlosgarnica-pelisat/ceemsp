package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.IncidenciaArma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaArmaRepository extends JpaRepository<IncidenciaArma, Integer> {
    List<IncidenciaArma> getAllByIncidenciaAndEliminadoFalse(int incidenciaId);
    List<IncidenciaArma> getAllByIncidencia(int incidenciaId);
    List<IncidenciaArma> getAllByIncidenciaAndEliminadoTrue(int incidenciaId);
    List<IncidenciaArma> getAllByArma(int arma);
    IncidenciaArma getByIncidenciaAndArmaAndEliminadoFalse(int incidencia, int arma);
    int countByIncidenciaAndEliminadoFalse(int incidencia);
}
