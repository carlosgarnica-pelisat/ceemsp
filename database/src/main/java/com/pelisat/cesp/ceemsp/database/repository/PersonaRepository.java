package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonaRepository extends JpaRepository<Personal, Integer> {
    List<Personal> getAllByEliminadoFalse();
    List<Personal> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Personal> getAllByEmpresaAndPuestoInAndEliminadoFalse(int empresa, List<Integer> puestos);
    List<Personal> getAllByEmpresaAndPuestoInAndEliminadoTrue(int empresa, List<Integer> puesto);
    List<Personal> getAllByEmpresaAndEliminadoTrue(int empresa);
    List<Personal> getAllByEmpresaAndClienteIsNullAndClienteDomicilioIsNullAndEliminadoFalse(int empresa);
    Personal getByUuidAndEliminadoFalse(String uuid);
    Personal getByUuid(String uuid);
    Personal getByCurpAndEliminadoFalse(String curp);
    Personal getByRfcAndEliminadoFalse(String rfc);
    Personal getByCanAndEliminadoFalse(int can);
    Personal getByVehiculoAndEliminadoFalse(int vehiculo);
    Personal getByArmaCortaAndEliminadoFalse(int armaCorta);
    Personal getByArmaLargaAndEliminadoFalse(int armaLarga);
    Personal getByCuipAndEliminadoFalse(String cuip);

    // Para puestos operativos
    List<Personal> getAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaFin);
    List<Personal> getAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Personal> getAllByEmpresaAndPuestoInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Para puestos no operativos
    List<Personal> getAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaFin);
    List<Personal> getAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Personal> getAllByEmpresaAndPuestoNotInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Personal> findAllByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Personal> findAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndPuestoInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Para puestos no operativos
    List<Personal> findAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndPuestoNotInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, List<Integer> puestos, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("select p from Personal p where CONCAT(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres) like %:nombres% or " +
            "CONCAT(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres) like %:apellidoPaterno% or " +
            "CONCAT(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres) like %:apellidoMaterno%")
    List<Personal> findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombresContainingAsQuery(
            @Param("apellidoPaterno")String apellidoPaterno,
            @Param("apellidoMaterno") String apellidoMaterno,
            @Param("nombres") String nombres);

    // Search
    List<Personal> findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombresContaining(String apellidoPaterno, String apellidoMaterno, String nombres);
    List<Personal> findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombresContainingAndEmpresa(String apellidoPaterno, String apellidoMaterno, String nombres, int empresa);
    List<Personal> findAllByRfcContaining(String rfc);
    List<Personal> findAllByRfcContainingAndEmpresa(String rfc, int empresa);
    List<Personal> findAllByCurpContaining(String curp);
    List<Personal> findAllByCurpContainingAndEmpresa(String curp, int empresa);
    List<Personal> findAllByCuipContaining(String cuip);
    List<Personal> findAllByCuipContainingAndEmpresa(String cuip, int empresa);
    List<Personal> findAllBySexo(SexoEnum sexo);

    List<Personal> getAllByEmpresaAndClienteDomicilioAndEliminadoFalse(int empresa, int clienteDomicilio);
    List<Personal> getAllByFechaCreacionGreaterThanEqualAndFechaCreacionLessThanEqualAndEliminadoFalse(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Personal> getAllByIdIn(List<Integer> ids);
}
