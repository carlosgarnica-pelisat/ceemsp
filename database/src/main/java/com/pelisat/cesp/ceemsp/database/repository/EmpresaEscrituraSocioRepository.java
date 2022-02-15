package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraSocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEscrituraSocioRepository extends JpaRepository<EmpresaEscrituraSocio, Integer> {
    List<EmpresaEscrituraSocio> findAllByEscrituraAndEliminadoFalse(int escritura);
    EmpresaEscrituraSocio findByUuidAndEliminadoFalse(String uuid);
}
