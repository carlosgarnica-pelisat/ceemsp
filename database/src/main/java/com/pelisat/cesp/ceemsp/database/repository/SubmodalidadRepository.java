package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Submodalidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmodalidadRepository extends JpaRepository<Submodalidad, Integer> {
    List<Submodalidad> getAllByCategoriaAndEliminadoFalse(int categoria);
    Submodalidad getByUuidAndEliminadoFalse(String uuid);
}
