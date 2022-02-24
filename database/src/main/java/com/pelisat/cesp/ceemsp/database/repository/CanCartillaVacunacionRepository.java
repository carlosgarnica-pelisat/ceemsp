package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.CanCartillaVacunacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanCartillaVacunacionRepository extends JpaRepository<CanCartillaVacunacion, Integer> {
    List<CanCartillaVacunacion> findAllByCanAndEliminadoFalse(int can);
    CanCartillaVacunacion findByUuidAndEliminadoFalse(String uuid);
}
