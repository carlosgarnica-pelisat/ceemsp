package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaReporteMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmpresaReporteMensualRepository extends JpaRepository<EmpresaReporteMensual, Integer> {
    List<EmpresaReporteMensual> getAllByEmpresaAndEliminadoFalse(int empresa);
    EmpresaReporteMensual getByUuidAndEliminadoFalse(String uuid);
    List<EmpresaReporteMensual> getAllByVentanaAndEliminadoFalse(Integer ventana);
    List<EmpresaReporteMensual> getAllByEmpresaAndVentanaAndEliminadoFalse(Integer empresa, Integer ventana);
    EmpresaReporteMensual findBySelloAndEliminadoFalse(String sello);

    EmpresaReporteMensual findFirstByEliminadoFalseOrderByFechaCreacionDesc();

    List<EmpresaReporteMensual> findByFechaCreacionGreaterThanAndFechaCreacionLessThan(LocalDateTime start, LocalDateTime end);
}
