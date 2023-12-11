package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoDomicilioRepository extends JpaRepository<VehiculoDomicilio, Integer> {
    List<VehiculoDomicilio> findAllByVehiculo(int vehiculo);
}
