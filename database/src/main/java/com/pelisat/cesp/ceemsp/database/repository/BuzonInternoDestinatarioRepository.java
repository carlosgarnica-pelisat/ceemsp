package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.BuzonInternoDestinatario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuzonInternoDestinatarioRepository extends JpaRepository<BuzonInternoDestinatario, Integer> {
    List<BuzonInternoDestinatario> getAllByBuzonInternoAndEliminadoFalse(int buzonInterno);
}
