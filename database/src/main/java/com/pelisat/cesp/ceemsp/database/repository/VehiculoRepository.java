package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.type.VehiculoStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
    Vehiculo getBySerieAndEliminadoFalse(String serie);
    Vehiculo getByPlacasAndEliminadoFalse(String placas);
    Vehiculo getByUuidAndEliminadoFalse(String uuid);
    Vehiculo getByUuidAndEliminadoTrue(String uuid);
    Vehiculo getByUuid(String uuid);
    List<Vehiculo> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Vehiculo> getAllByEmpresaAndEliminadoTrue(int empresa);
    List<Vehiculo> getAllByEmpresa(int empresa);
    List<Vehiculo> getAllByEmpresaAndStatusAndEliminadoFalse(int empresa, VehiculoStatusEnum status);
    int countByEmpresaAndEliminadoFalse(int empresa);
    int countByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    int countByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    int countByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    List<Vehiculo> findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    List<Vehiculo> findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    // Search
    List<Vehiculo> findAllByPlacasContaining(String placas);
    List<Vehiculo> findAllByPlacasContainingAndEmpresa(String placas, int empresa);
    List<Vehiculo> findAllBySerieContaining(String serie);
    List<Vehiculo> findAllBySerieContainingAndEmpresa(String serie, int empresa);
    List<Vehiculo> findAllByNumeroHologramaContaining(String numeroHolograma);
    List<Vehiculo> findAllByNumeroHologramaContainingAndEmpresa(String numeroHolograma, int empresa);
    List<Vehiculo> findAllByEmpresaBlindajeContaining(String empresaBlindaje);
    List<Vehiculo> findAllByEmpresaBlindajeContainingAndEmpresa(String empresaBlindaje, int empresa);
    List<Vehiculo> findAllByEliminadoFalse();
}
