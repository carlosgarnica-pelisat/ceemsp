package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    List<Incidencia> findAllByEmpresaAndEliminadoFalse(int empresa);
    Incidencia getByUuidAndEliminadoFalse(String uuid);
}
