package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.VehiculoMarca;
import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoMarcaRepository extends JpaRepository<VehiculoMarca, Integer> {
    List<VehiculoMarca> getAllByEliminadoFalse();
    List<VehiculoMarca> getAllByTipoAndEliminadoFalse(VehiculoTipoEnum vehiculoTipoEnum);
    VehiculoMarca getByUuidAndEliminadoFalse(String uuid);
    VehiculoMarca getByIdAndEliminadoFalse(Integer id);
}
