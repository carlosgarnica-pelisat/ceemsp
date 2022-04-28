package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.IncidenciaArma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaArmaRepository extends JpaRepository<IncidenciaArma, Integer> {
    List<IncidenciaArma> getAllByIncidenciaAndEliminadoFalse(int incidenciaId);
    IncidenciaArma getByIncidenciaAndArmaAndEliminadoFalse(int incidencia, int arma);
}
