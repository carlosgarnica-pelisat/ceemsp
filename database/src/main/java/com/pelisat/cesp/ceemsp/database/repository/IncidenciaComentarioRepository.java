package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.IncidenciaComentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaComentarioRepository extends JpaRepository<IncidenciaComentario, Integer> {
    List<IncidenciaComentario> getAllByIncidenciaAndEliminadoFalse(int incidenciaId);
}
