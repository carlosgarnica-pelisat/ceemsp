package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalNacionalidad;
import com.pelisat.cesp.ceemsp.database.model.PersonalPuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalNacionalidadRepository extends JpaRepository<PersonalNacionalidad, Integer> {
    List<PersonalNacionalidad> getAllByEliminadoFalse();
    List<PersonalNacionalidad> getAllByEliminadoFalseOrderByNombre();

    PersonalNacionalidad getByUuidAndEliminadoFalse(String uuid);
}
