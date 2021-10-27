package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Modalidad;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModalidadRepository extends JpaRepository<Modalidad, Integer> {
    List<Modalidad> findAllByEliminadoFalse();

    Modalidad findByUuidAndEliminadoFalse(String uuid);

    List<Modalidad> findAllByTipoAndEliminadoFalse(TipoTramiteEnum tipo);
}
