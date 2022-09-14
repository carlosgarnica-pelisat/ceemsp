package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraApoderado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEscrituraApoderadoRepository extends JpaRepository<EmpresaEscrituraApoderado, Integer> {
    List<EmpresaEscrituraApoderado> findAllByEscrituraAndEliminadoFalse(int escritura);
    List<EmpresaEscrituraApoderado> findAllByEscritura(int escritura);
    EmpresaEscrituraApoderado findByUuidAndEliminadoFalse(String uuid);
}
