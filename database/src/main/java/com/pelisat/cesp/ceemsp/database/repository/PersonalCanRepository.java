package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalCan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalCanRepository extends JpaRepository<PersonalCan, Integer> {
    PersonalCan getByPersonalAndEliminadoFalse(int personal);
    List<PersonalCan> getAllByCan(int can);
}
