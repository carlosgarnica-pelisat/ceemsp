package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilioTelefono;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaDomicilioTelefonoRepository extends JpaRepository<EmpresaDomicilioTelefono, Integer> {
    List<EmpresaDomicilioTelefono> findAllByDomicilioAndEliminadoFalse(int domicilio);
    EmpresaDomicilioTelefono findByUuidAndEliminadoFalse(String uuid);
}
