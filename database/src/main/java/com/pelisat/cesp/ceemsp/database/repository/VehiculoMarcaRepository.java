package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoMarca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoMarcaRepository extends JpaRepository<VehiculoMarca, Integer> {
    List<VehiculoMarca> getAllByEliminadoFalse();

    VehiculoMarca getByUuidAndEliminadoFalse(String uuid);

    VehiculoMarca getByIdAndEliminadoFalse(Integer id);
}
