package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Personal, Integer> {
    List<Personal> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Personal> getAllByEmpresaAndEliminadoTrue(int empresa);
    Personal getByUuidAndEliminadoFalse(String uuid);
    Personal getByUuid(String uuid);
    Personal getByCurpAndEliminadoFalse(String curp);
}
