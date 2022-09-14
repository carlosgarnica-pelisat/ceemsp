package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ClienteAsignacionPersonal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteAsignacionPersonalRepository extends JpaRepository<ClienteAsignacionPersonal, Integer> {
    List<ClienteAsignacionPersonal> getAllByClienteAndEliminadoFalse(int cliente);
    ClienteAsignacionPersonal getByUuidAndEliminadoFalse(String uuid);
}
