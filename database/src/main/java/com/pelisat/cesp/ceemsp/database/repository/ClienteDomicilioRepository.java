package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ClienteDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteDomicilioRepository extends JpaRepository<ClienteDomicilio, Integer> {
    List<ClienteDomicilio> getAllByClienteAndEliminadoFalse(int cliente);
    ClienteDomicilio findByUuidAndEliminadoFalse(String uuid);
    ClienteDomicilio findByClienteAndMatrizTrueAndEliminadoFalse(int cliente);
}
