package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraConsejo;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraSocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEscrituraConsejoRepository extends JpaRepository<EmpresaEscrituraConsejo, Integer> {
    List<EmpresaEscrituraConsejo> findAllByEscrituraAndEliminadoFalse(int escritura);
    List<EmpresaEscrituraConsejo> findAllByEscritura(int escritura);
    EmpresaEscrituraConsejo findByUuidAndEliminadoFalse(String uuid);
    EmpresaEscrituraConsejo findByUuid(String uuid);
}
