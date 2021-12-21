package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Arma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArmaRepository extends JpaRepository<Arma, Integer> {
    List<Arma> getAllByEmpresaAndEliminadoFalse(int empresa);

    List<Arma> getAllByLicenciaColectivaAndEliminadoFalse(int licenciaColectiva);

    Arma getByUuidAndEliminadoFalse(String uuid);
}
