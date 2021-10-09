package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoTipoRepository extends JpaRepository<VehiculoTipo, Integer> {
    List<VehiculoTipo> getAllByEliminadoFalse();
}
