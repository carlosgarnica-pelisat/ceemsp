package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CanRepository extends JpaRepository<Can, Integer> {
    List<Can> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Can> getAllByEmpresaAndEliminadoTrue(int empresa);
    List<Can> getAllByEmpresa(int empresa);
    List<Can> getAllByEmpresaAndStatus(int empresa, CanStatusEnum status);
    Can getByUuidAndEliminadoFalse(String uuid);
    Can getByUuid(String uuid);

    @Query(value = "select count(*) from Can c where c.empresa = :empresa and c.fechaCreacion < :fechaFin and c.fotografiaCapturada = true and c.adiestramientoCapturado = true and (c.constanciaCapturada = true or c.vacunacionCapturada = true) and c.eliminado = false")
    Integer countByEmpresaAndFechaCreacionLessThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalse(Integer empresa, LocalDateTime fechaFin);
    @Query(value = "select count(*) from Can c where c.empresa = :empresa and c.fechaCreacion between :fechaInicio and :fechaFin and c.fotografiaCapturada = true and c.adiestramientoCapturado = true and (c.constanciaCapturada = true or c.vacunacionCapturada = true) and c.eliminado = false")
    Integer countByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalse(Integer empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    @Query(value = "select count(*) from Can c where c.empresa = :empresa and c.fechaActualizacion between :fechaInicio and :fechaFin and c.fotografiaCapturada = true and c.adiestramientoCapturado = true and (c.constanciaCapturada = true or c.vacunacionCapturada = true) and c.eliminado = true")
    Integer countByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoTrue(Integer empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    @Query(value = "from Can c where c.empresa = :empresa and c.fechaCreacion < :fechaFin and c.fotografiaCapturada = true and c.adiestramientoCapturado = true and (c.constanciaCapturada = true or c.vacunacionCapturada = true) and c.eliminado = false")
    List<Can> findAllByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    @Query(value = "from Can c where c.empresa = :empresa and c.fechaCreacion between :fechaInicio and :fechaFin and c.fotografiaCapturada = true and c.adiestramientoCapturado = true and (c.constanciaCapturada = true or c.vacunacionCapturada = true) and c.eliminado = false")
    List<Can> findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalseAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    @Query(value = "from Can c where c.empresa = :empresa and c.fechaActualizacion between :fechaInicio and :fechaFin and c.fotografiaCapturada = true and c.adiestramientoCapturado = true and (c.constanciaCapturada = true or c.vacunacionCapturada = true) and c.eliminado = true")
    List<Can> findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    // Search
    List<Can> findAllByNombreContaining(String nombre);
    List<Can> findAllByNombreContainingAndEmpresa(String nombre, int empresa);
    List<Can> findAllByEliminadoFalse();
    List<Can> getAllByFechaCreacionGreaterThanEqualAndFechaCreacionLessThanEqualAndEliminadoFalse(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
