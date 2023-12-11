package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findAllByEmpresaAndEliminadoFalse(int empresa);
    List<Cliente> findAllByEmpresaAndEliminadoTrue(int empresa);
    Cliente findByUuidAndEliminadoFalse(String uuid);
    Cliente findByUuid(String uuid);
    int countByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    int countByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(int empresa, LocalDate fechaInicio, LocalDate fechaFin);
    List<Cliente> findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    List<Cliente> findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin);
    int countByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);
    int countByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int empresa, LocalDateTime fechaFin, LocalDateTime fechaInicio);

    // Search
    List<Cliente> findAllByRazonSocialContainingOrNombreComercialContaining(String razonSocial, String nombreComercial);
    List<Cliente> findAllByRazonSocialContainingOrNombreComercialContainingAndEmpresa(String razonSocial, String nombreComercial, int empresa);
    List<Cliente> findAllByRfcContaining(String rfc);
    List<Cliente> findAllByRfcContainingAndEmpresa(String rfc, int empresa);
    List<Cliente> findAllByEliminadoFalse();
}
