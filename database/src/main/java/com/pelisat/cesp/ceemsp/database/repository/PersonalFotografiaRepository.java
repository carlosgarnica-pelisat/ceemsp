package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalFotografia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalFotografiaRepository extends JpaRepository<PersonalFotografia, Integer> {
    List<PersonalFotografia> getAllByPersonalAndEliminadoFalse(int personaId);
    PersonalFotografia getByUuidAndEliminadoFalse(String uuid);
}
