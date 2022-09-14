package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findAllByEmpresaAndEliminadoFalse(int empresa);
    List<Cliente> findAllByEmpresaAndEliminadoTrue(int empresa);
    Cliente findByUuidAndEliminadoFalse(String uuid);
    Cliente findByUuid(String uuid);
}
