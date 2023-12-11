package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Integer> {
    List<Visita> getAllByEliminadoFalse();
    List<Visita> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Visita> getAllByFechaVisitaGreaterThanEqualAndEliminadoFalse(LocalDate fecha);
    List<Visita> getAllByFechaVisitaGreaterThanAndFechaVisitaLessThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);
    List<Visita> getAllByFechaVisitaGreaterThanAndEmpresaAndFechaVisitaLessThanAndEliminadoFalse(LocalDate fechaInicio, int empresa, LocalDate fechaFin);
    List<Visita> getAllByFechaTerminoGreaterThanEqualAndEliminadoFalse(LocalDate fecha);
    @Query(value = "from Visita v where v.fechaTermino between :fechaInicio and :fechaFin")
    List<Visita> getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);

    @Query(value = "from Visita v where v.fechaTermino between :fechaInicio and :fechaFin and v.empresa = :empresa")
    List<Visita> getAllByEmpresaAndFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(int empresa, LocalDate fechaInicio, LocalDate fechaFin);
    List<Visita> getAllByEmpresaAndFechaTerminoGreaterThanEqualAndEliminadoFalse(int empresa, LocalDate fecha);
    Visita findByUuidAndEliminadoFalse(String uuid);
    Visita findFirstByEliminadoFalseOrderByFechaCreacionDesc();
    Visita findFirstByTipoVisitaAndEliminadoFalseOrderByFechaCreacionDesc(TipoVisitaEnum tipoVisita);
}
