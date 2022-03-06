package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Colonia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColoniaRepository extends JpaRepository<Colonia, Integer> {
    List<Colonia> getAllByEstadoAndMunicipioAndEliminadoFalse(int estado, int municipio);
}
