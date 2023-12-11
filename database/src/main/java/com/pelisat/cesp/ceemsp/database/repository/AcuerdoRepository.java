package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Acuerdo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AcuerdoRepository extends JpaRepository<Acuerdo, Integer> {
    List<Acuerdo> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Acuerdo> getAllByEliminadoFalse();
    List<Acuerdo> getAllByEmpresaAndEliminadoTrue(int empresa);
    @Query(value = "from Acuerdo v where v.fechaFin between :fechaInicio and :fechaFin")
    List<Acuerdo> getAllByFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);
    List<Acuerdo> getAllByEmpresaAndFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(int empresa, LocalDate fechaFinLess, LocalDate fechaFinGreater);
    List<Acuerdo> getAllByEmpresaAndFechaFinGreaterThanEqualAndEliminadoFalse(int empresa, LocalDate localDate);
    Acuerdo getByUuid(String uuid);
}
