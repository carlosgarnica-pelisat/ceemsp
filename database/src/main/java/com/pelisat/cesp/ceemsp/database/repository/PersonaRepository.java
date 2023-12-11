package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonaRepository extends JpaRepository<Personal, Integer> {
    List<Personal> getAllByEliminadoFalse();
    List<Personal> getAllByEmpresaAndEliminadoFalse(int empresa);
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

    int countByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    int countByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    int countByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    int countByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    int countByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    int countByEmpresaAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Personal> findAllByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Personal> findAllByEmpresaAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaInicio, LocalDateTime fechaFin);

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
}
