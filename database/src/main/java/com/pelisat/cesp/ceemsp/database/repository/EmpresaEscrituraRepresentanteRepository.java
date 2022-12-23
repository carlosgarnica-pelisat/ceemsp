package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraRepresentante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEscrituraRepresentanteRepository extends JpaRepository<EmpresaEscrituraRepresentante, Integer> {
    List<EmpresaEscrituraRepresentante> findAllByEscrituraAndEliminadoFalse(int escritura);
    List<EmpresaEscrituraRepresentante> findAllByEscritura(int escritura);
    EmpresaEscrituraRepresentante findByUuidAndEliminadoFalse(String uuid);
    EmpresaEscrituraRepresentante findByUuid(String uuid);
}
