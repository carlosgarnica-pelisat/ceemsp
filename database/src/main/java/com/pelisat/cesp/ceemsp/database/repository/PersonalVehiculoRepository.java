package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalVehiculoRepository extends JpaRepository<PersonalVehiculo, Integer> {
    PersonalVehiculo getByPersonalAndEliminadoFalse(int personal);
    List<PersonalVehiculo> getAllByVehiculo(int vehiculo);
}
