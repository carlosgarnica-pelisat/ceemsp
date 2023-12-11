package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.EmpresaLicenciaColectiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmpresaLicenciaColectivaRepository extends JpaRepository<EmpresaLicenciaColectiva, Integer> {
    List<EmpresaLicenciaColectiva> findAllByEmpresaAndEliminadoFalse(Integer empresa);
    List<EmpresaLicenciaColectiva> findAllByEmpresaAndEliminadoTrue(Integer empresa);
    EmpresaLicenciaColectiva findByUuidAndEliminadoFalse(String uuid);
    EmpresaLicenciaColectiva findByUuid(String uuid);
    List<EmpresaLicenciaColectiva> findAllByEliminadoFalse();
    List<EmpresaLicenciaColectiva> findAllByEliminadoTrue();
    List<EmpresaLicenciaColectiva> getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);

    @Query(value = "from EmpresaLicenciaColectiva v where v.fechaFin between :fechaInicio and :fechaFin and v.empresa = :empresa")
    List<EmpresaLicenciaColectiva> getAllByEmpresaAndFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin, int empresa);
}
