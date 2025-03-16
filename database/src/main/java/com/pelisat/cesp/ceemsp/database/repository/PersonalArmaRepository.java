package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalArma;
import com.pelisat.cesp.ceemsp.database.model.PersonalCan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalArmaRepository extends JpaRepository<PersonalArma, Integer> {
    PersonalArma getByPersonalAndArmaAndEliminadoFalse(int personal, int arma);
    List<PersonalArma> getAllByPersonalAndEliminadoFalse(int personal);
    List<PersonalArma> getAllByArma(int arma);
}
