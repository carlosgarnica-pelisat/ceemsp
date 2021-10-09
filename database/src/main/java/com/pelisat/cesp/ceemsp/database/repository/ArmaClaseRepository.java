package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ArmaClase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArmaClaseRepository extends JpaRepository<ArmaClase, Integer> {
    List<ArmaClase> getAllByEliminadoFalse();

    ArmaClase getByUuidAndEliminadoFalse(String uuid);
}
