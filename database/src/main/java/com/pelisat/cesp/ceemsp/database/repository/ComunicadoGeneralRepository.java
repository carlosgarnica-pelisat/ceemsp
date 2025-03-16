package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ComunicadoGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ComunicadoGeneralRepository extends JpaRepository<ComunicadoGeneral, Integer> {
    List<ComunicadoGeneral> getAllByEliminadoFalseOrderByFechaPublicacion();
    List<ComunicadoGeneral> getAllByEliminadoFalseOrderByFechaPublicacionDesc();
    ComunicadoGeneral getByUuidAndEliminadoFalse(String uuid);
    List<ComunicadoGeneral> getTop1ByFechaPublicacionBeforeAndEliminadoFalseOrderByFechaPublicacionDesc(LocalDate time);

    List<ComunicadoGeneral> getAllByFechaPublicacionBetweenAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("select c from ComunicadoGeneral c where c.fechaPublicacion between ?1 and ?2 and c.titulo like %?3%")
    List<ComunicadoGeneral> getAllByFechaPublicacionAndTitulo(LocalDate fechaInicio, LocalDate fechaFin, String titulo);

    @Query("select c from ComunicadoGeneral c where c.titulo like %?1%")
    List<ComunicadoGeneral> getAllByComunicadoGeneral(String titulo);
}