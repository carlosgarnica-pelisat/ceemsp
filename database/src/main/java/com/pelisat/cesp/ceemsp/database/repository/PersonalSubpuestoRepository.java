package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalSubpuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalSubpuestoRepository extends JpaRepository<PersonalSubpuesto, Integer> {
    List<PersonalSubpuesto> getAllByPuestoAndEliminadoFalse(int puesto);

    PersonalSubpuesto getByUuidAndEliminadoFalse(String uuid);
}
