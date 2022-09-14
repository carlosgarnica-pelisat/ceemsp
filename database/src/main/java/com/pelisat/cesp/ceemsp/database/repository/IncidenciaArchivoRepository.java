package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.IncidenciaArchivo;
import com.pelisat.cesp.ceemsp.database.model.IncidenciaArma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaArchivoRepository extends JpaRepository<IncidenciaArchivo, Integer> {
    List<IncidenciaArchivo> getAllByIncidenciaAndEliminadoFalse(int incidenciaId);
    IncidenciaArchivo getByUuidAndEliminadoFalse(String uuid);
}
