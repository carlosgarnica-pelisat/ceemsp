package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ArmaMarca;
import com.pelisat.cesp.ceemsp.database.model.ArmaTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArmaTipoRepository extends JpaRepository<ArmaTipo, Integer> {
    List<ArmaTipo> getAllByEliminadoFalse();

    ArmaTipo getByUuidAndEliminadoFalse(String uuid);
}
