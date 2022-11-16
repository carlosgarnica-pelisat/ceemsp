package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ClienteModalidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteModalidadRepository extends JpaRepository<ClienteModalidad, Integer> {
    List<ClienteModalidad> getAllByClienteAndEliminadoFalse(int cliente);
    ClienteModalidad getByUuidAndEliminadoFalse(String uuid);
}
