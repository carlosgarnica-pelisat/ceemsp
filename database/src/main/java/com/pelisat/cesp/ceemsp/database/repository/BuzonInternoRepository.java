package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.BuzonInterno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuzonInternoRepository extends JpaRepository<BuzonInterno, Integer> {
    BuzonInterno getByUuidAndEliminadoFalse(String uuid);
    BuzonInterno getByIdAndEliminadoFalse(Integer id);
    List<BuzonInterno> getAllByEliminadoFalse();
}
