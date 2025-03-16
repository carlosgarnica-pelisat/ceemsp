package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.IncidenciaVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaVehiculoRepository extends JpaRepository<IncidenciaVehiculo, Integer> {
    List<IncidenciaVehiculo> getAllByIncidenciaAndEliminadoFalse(int incidenciaId);
    IncidenciaVehiculo getByIncidenciaAndVehiculoAndEliminadoFalse(int incidencia, int vehiculo);
    int countByIncidenciaAndEliminadoFalse(int incidencia);
}
