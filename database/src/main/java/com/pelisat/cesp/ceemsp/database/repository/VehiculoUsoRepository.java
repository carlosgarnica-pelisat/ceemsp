package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoUso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoUsoRepository extends JpaRepository<VehiculoUso, Integer> {
    List<VehiculoUso> getAllByEliminadoFalse();

    VehiculoUso getByUuidAndEliminadoFalse(String uuid);
}
