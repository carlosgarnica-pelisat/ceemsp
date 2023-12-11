package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Incidencia;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    List<Incidencia> findAllByEmpresaAndEliminadoFalse(int empresa);
    Incidencia getByUuidAndEliminadoFalse(String uuid);
    Incidencia getByUuidAndEliminadoTrue(String uuid);
    Integer countAllByStatusAndEliminadoFalse(IncidenciaStatusEnum incidenciaStatusEnum);
    List<Incidencia> getAllByStatusAndAsignadoAndEliminadoFalse(IncidenciaStatusEnum incidenciaStatusEnum, int usuarioAsignado);
    List<Incidencia> getAllByStatusAndEliminadoFalse(IncidenciaStatusEnum incidenciaStatusEnum);
    Integer countAllByStatusAndEmpresaAndEliminadoFalse(IncidenciaStatusEnum status, int empresa);

    Integer countAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    Integer countAllByEmpresaAndStatusAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, IncidenciaStatusEnum status, LocalDateTime fechaFin, LocalDateTime fechaInicio);


    // Search
    List<Incidencia> findAllByNumeroContaining(String numero);
    List<Incidencia> findAllByNumeroContainingAndEmpresa(String numero, int empresa);
}
