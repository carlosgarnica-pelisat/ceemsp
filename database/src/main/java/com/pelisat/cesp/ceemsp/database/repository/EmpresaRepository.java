package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    List<Empresa> getAllByEliminadoFalse();
    List<Empresa> getAllByStatus(EmpresaStatusEnum status);
    List<Empresa> getAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum tipoTramiteEnum);
    List<Empresa> getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate fecha, LocalDate fechaOtra);
    Empresa getByUuidAndEliminadoFalse(String uuid);
    Empresa findFirstByTipoTramiteOrderByFechaCreacionDesc(TipoTramiteEnum tipoTramiteEnum);
    Empresa getByRfcAndEliminadoFalse(String rfc);
    Empresa getByCurpAndEliminadoFalse(String curp);
    Empresa getByRegistroAndEliminadoFalse(String registro);
    Empresa getFirstByRegistroAndEliminadoFalse(String registro);

    Integer countAllByStatusAndEliminadoFalse(EmpresaStatusEnum status);
    Integer countAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum tipoTramiteEnum);
    Integer countAllByEliminadoFalse();

    // Search
    List<Empresa> findAllByRazonSocialContainingOrNombreComercialContaining(String razonSocial, String nombreComercial);
    List<Empresa> findAllByRfcContaining(String rfc);
    List<Empresa> findAllByCurpContaining(String curp);
    List<Empresa> findAllByCorreoElectronicoContaining(String correoElectronico);
    List<Empresa> findAllByRegistroContaining(String registro);
    List<Empresa> findAllByRegistroFederalContaining(String registroFederal);

    @Query(value = "from Empresa v where v.fechaFin between :fechaInicio and :fechaFin")
    List<Empresa> getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);


    // Consultas auditoria
    List<Empresa> getAllByCreadoPor(int usuarioId);
    List<Empresa> getAllByActualizadoPor(int usuarioId);
}
