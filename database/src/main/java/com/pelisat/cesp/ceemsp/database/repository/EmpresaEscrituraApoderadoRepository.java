package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraApoderado;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmpresaEscrituraApoderadoRepository extends JpaRepository<EmpresaEscrituraApoderado, Integer> {
    List<EmpresaEscrituraApoderado> findAllByEscrituraAndEliminadoFalse(int escritura);
    List<EmpresaEscrituraApoderado> findAllByEscritura(int escritura);
    EmpresaEscrituraApoderado findByUuidAndEliminadoFalse(String uuid);
    EmpresaEscrituraApoderado findByUuid(String uuid);
    List<EmpresaEscrituraApoderado> getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);

    @Query(value = "from EmpresaEscrituraApoderado v where v.fechaFin between :fechaInicio and :fechaFin")
    List<EmpresaEscrituraApoderado> getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);
}
