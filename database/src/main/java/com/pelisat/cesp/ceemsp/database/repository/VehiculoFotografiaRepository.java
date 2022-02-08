package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoFotografia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoFotografiaRepository extends JpaRepository<VehiculoFotografia, Integer> {
    List<VehiculoFotografia> getAllByVehiculoAndEliminadoFalse(int vehiculoId);
    VehiculoFotografia getByUuidAndEliminadoFalse(String uuid);
}
