package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ArmaMarca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArmaMarcaRepository extends JpaRepository<ArmaMarca, Integer> {
    List<ArmaMarca> getAllByEliminadoFalse();

    ArmaMarca getByUuidAndEliminadoFalse(String uuid);
}
