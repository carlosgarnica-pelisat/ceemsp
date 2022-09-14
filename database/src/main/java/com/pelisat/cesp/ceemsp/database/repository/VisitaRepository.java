package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Integer> {
    List<Visita> getAllByEliminadoFalse();
    List<Visita> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Visita> getAllByFechaVisitaGreaterThanEqualAndEliminadoFalse(LocalDate fecha);
    Visita findByUuidAndEliminadoFalse(String uuid);
    Visita findFirstByEliminadoFalseOrderByFechaCreacionDesc();
}
