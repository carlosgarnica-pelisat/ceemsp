package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Uniforme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniformeRepository extends JpaRepository<Uniforme, Integer> {
    List<Uniforme> findAllByEliminadoFalse();

    Uniforme findByUuidAndEliminadoFalse(String uuid);
}
