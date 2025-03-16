package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Ventana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VentanaRepository extends JpaRepository<Ventana, Integer> {
    List<Ventana> getAllByEliminadoFalse();
    Ventana getByUuidAndEliminadoFalse(String uuid);
    Ventana getByFechaFinGreaterThanEqualAndEliminadoFalse(LocalDate fechaFin);
}
