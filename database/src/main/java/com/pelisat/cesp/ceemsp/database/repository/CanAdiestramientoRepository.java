package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.CanAdiestramiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanAdiestramientoRepository extends JpaRepository<CanAdiestramiento, Integer> {
    List<CanAdiestramiento> findAllByCanAndEliminadoFalse(int id);
    CanAdiestramiento findByUuidAndEliminadoFalse(String uuid);
}
