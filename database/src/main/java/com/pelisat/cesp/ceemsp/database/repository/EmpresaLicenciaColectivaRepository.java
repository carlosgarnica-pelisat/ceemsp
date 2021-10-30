package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaLicenciaColectiva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaLicenciaColectivaRepository extends JpaRepository<EmpresaLicenciaColectiva, Integer> {
    List<EmpresaLicenciaColectiva> findAllByEmpresaAndEliminadoFalse(Integer empresa);
}
