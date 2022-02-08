package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.CanFotografia;
import com.pelisat.cesp.ceemsp.database.model.VehiculoFotografia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanFotografiaRepository extends JpaRepository<CanFotografia, Integer> {
    List<CanFotografia> getAllByCanAndEliminadoFalse(int canId);
    CanFotografia getByUuidAndEliminadoFalse(String uuid);
}
