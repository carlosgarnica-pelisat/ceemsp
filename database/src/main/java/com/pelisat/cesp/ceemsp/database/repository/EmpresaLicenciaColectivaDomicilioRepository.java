package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaLicenciaColectivaDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaLicenciaColectivaDomicilioRepository extends JpaRepository<EmpresaLicenciaColectivaDomicilio, Integer> {
    List<EmpresaLicenciaColectivaDomicilio> findAllByLicenciaColectivaAndEliminadoFalse(int licenciaColectiva);
    EmpresaLicenciaColectivaDomicilio findByUuidAndEliminadoFalse(String uuid);
    EmpresaLicenciaColectivaDomicilio findByLicenciaColectivaAndDomicilioAndEliminadoFalse(int licenciaColectiva, int domicilio);
}
