package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ClienteFormaEjecucion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteFormaEjecucionRepository extends JpaRepository<ClienteFormaEjecucion, Integer> {
    List<ClienteFormaEjecucion> getAllByClienteAndEliminadoFalse(int cliente);
    ClienteFormaEjecucion getByUuidAndEliminadoFalse(String uuid);
}
