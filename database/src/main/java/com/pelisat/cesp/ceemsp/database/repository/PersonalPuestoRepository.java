package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalPuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalPuestoRepository extends JpaRepository<PersonalPuesto, Integer> {
    List<PersonalPuesto> getAllByEliminadoFalse();

    PersonalPuesto getByUuidAndEliminadoFalse(String uuid);
}
