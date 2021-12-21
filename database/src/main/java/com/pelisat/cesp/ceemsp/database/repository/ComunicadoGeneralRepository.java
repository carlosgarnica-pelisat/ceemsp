package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ComunicadoGeneral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ComunicadoGeneralRepository extends JpaRepository<ComunicadoGeneral, Integer> {
    List<ComunicadoGeneral> getAllByEliminadoFalseOrderByFechaPublicacion();
    List<ComunicadoGeneral> getAllByEliminadoFalseOrderByFechaPublicacionDesc();
    ComunicadoGeneral getByUuidAndEliminadoFalse(String uuid);
    List<ComunicadoGeneral> getTop1ByFechaPublicacionBeforeAndEliminadoFalseOrderByFechaPublicacion(LocalDate time);
}
