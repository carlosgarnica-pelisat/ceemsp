package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaDomicilioRepository extends JpaRepository<EmpresaDomicilio, Integer> {
    List<EmpresaDomicilio> findAllByEmpresaAndEliminadoFalse(int empresa);
    EmpresaDomicilio findByUuidAndEliminadoFalse(String uuid);
}
