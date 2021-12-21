package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
    Vehiculo getBySerieAndEliminadoFalse(String serie);

    Vehiculo getByPlacasAndEliminadoFalse(String placas);

    Vehiculo getByUuidAndEliminadoFalse(String uuid);

    List<Vehiculo> getAllByEmpresaAndEliminadoFalse(int empresa);
}
