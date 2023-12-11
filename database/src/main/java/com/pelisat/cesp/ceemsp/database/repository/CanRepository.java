package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CanRepository extends JpaRepository<Can, Integer> {
    List<Can> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Can> getAllByEmpresaAndEliminadoTrue(int empresa);
    List<Can> getAllByEmpresa(int empresa);
    List<Can> getAllByEmpresaAndStatus(int empresa, CanStatusEnum status);
    Can getByUuidAndEliminadoFalse(String uuid);
    Can getByUuid(String uuid);
    int countByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    int countByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    List<Can> findAllByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    List<Can> findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    List<Can> findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    // Search
    List<Can> findAllByNombreContaining(String nombre);
    List<Can> findAllByNombreContainingAndEmpresa(String nombre, int empresa);
    List<Can> findAllByEliminadoFalse();
}
