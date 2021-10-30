package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoSubmarca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoSubmarcaRepository extends JpaRepository<VehiculoSubmarca, Integer> {
    List<VehiculoSubmarca> getAllByEliminadoFalse();

    List<VehiculoSubmarca> getAllByMarcaAndEliminadoFalse(int marca);
}
