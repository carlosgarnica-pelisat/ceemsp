package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.CanTipoAdiestramiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanTipoAdiestramientoRepository extends JpaRepository<CanTipoAdiestramiento, Integer> {
    List<CanTipoAdiestramiento> getAllByEliminadoFalse();

    CanTipoAdiestramiento getByUuidAndEliminadoFalse(String uuid);
}
