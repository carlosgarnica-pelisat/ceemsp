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

    @Query("select c from ComunicadoGeneral c where year(c.fechaActualizacion) = ?1 and month(c.fechaActualizacion) = ?2")
    List<ComunicadoGeneral> getAllByFechaActualizacion(Integer year, Integer month);

    @Query("select c from ComunicadoGeneral c where year(c.fechaActualizacion) = ?1 and month(c.fechaActualizacion) = ?2 and c.titulo like %?3%")
    List<ComunicadoGeneral> getAllByFechaActualizacionAndTitulo(Integer year, Integer month, String titulo);
}