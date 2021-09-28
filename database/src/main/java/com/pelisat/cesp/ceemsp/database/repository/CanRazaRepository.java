package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.CanRaza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanRazaRepository extends JpaRepository<CanRaza, Integer> {
    List<CanRaza> getAllByEliminadoFalse();

    CanRaza getByUuidAndEliminadoFalse(String uuid);
}
