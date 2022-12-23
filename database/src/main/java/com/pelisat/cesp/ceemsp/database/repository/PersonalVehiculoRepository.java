package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalVehiculoRepository extends JpaRepository<PersonalVehiculo, Integer> {
    PersonalVehiculo getByPersonalAndEliminadoFalse(int personal);
}
