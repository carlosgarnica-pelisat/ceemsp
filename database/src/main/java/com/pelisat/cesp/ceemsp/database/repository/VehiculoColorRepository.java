package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoColor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoColorRepository extends JpaRepository<VehiculoColor, Integer> {
    List<VehiculoColor> getAllByVehiculoAndEliminadoFalse(int vehiculo);
    VehiculoColor findByUuidAndEliminadoFalse(String uuid);
}
