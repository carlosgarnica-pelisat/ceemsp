package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.CanCartillaVacunacion;
import com.pelisat.cesp.ceemsp.database.model.CanConstanciaSalud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanConstanciaSaludRepository extends JpaRepository<CanConstanciaSalud, Integer> {
    List<CanConstanciaSalud> findAllByCanAndEliminadoFalse(int can);
    List<CanConstanciaSalud> findAllByCan(int can);
    CanConstanciaSalud findByUuidAndEliminadoFalse(String uuid);
}
