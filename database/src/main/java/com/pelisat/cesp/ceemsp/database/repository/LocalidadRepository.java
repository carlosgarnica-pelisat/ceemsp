package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalidadRepository extends JpaRepository<Localidad, Integer> {
    List<Localidad> getAllByEstadoAndMunicipioAndEliminadoFalse(int estado, int municipio);
}
