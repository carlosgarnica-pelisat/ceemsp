package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraConsejo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEscrituraConsejoRepository extends JpaRepository<EmpresaEscrituraConsejo, Integer> {
    List<EmpresaEscrituraConsejo> findAllByEscrituraAndEliminadoFalse(int escritura);
}
