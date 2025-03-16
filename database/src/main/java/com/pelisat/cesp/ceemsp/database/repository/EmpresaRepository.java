package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import java.time.LocalDate;
import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    List<Empresa> getAllByEliminadoFalse();
    List<Empresa> getAllByStatus(EmpresaStatusEnum status);
    List<Empresa> getAllByStatusAndEliminadoFalse(EmpresaStatusEnum status);
    List<Empresa> getAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum tipoTramiteEnum);
    List<Empresa> getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate fecha, LocalDate fechaOtra);
    Empresa getByUuidAndEliminadoFalse(String uuid);
    Empresa findFirstByTipoTramiteOrderByFechaCreacionDesc(TipoTramiteEnum tipoTramiteEnum);

    @Query(value = "from Empresa e where e.registro like :query order by e.registro desc")
    List<Empresa> findFirstByTramiteOrderByTramite(String query);
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
    int countByRfcContainingAndStatusIn(String rfc, List<EmpresaStatusEnum> status);
    int countByCurpContainingAndStatusIn(String rfc, List<EmpresaStatusEnum> status);

    Empresa findFirstByRfcContainingAndStatusIn(String rfc, List<EmpresaStatusEnum> status);
    Empresa findFirstByCurpContainingAndStatusIn(String rfc, List<EmpresaStatusEnum> status);
    List<Empresa> findAllByCurpContaining(String curp);
    List<Empresa> findAllByCorreoElectronicoContaining(String correoElectronico);
    List<Empresa> findAllByRegistroContaining(String registro);
    List<Empresa> findAllByRegistroFederalContaining(String registroFederal);

    @Query(value = "from Empresa v where v.fechaFin between :fechaInicio and :fechaFin")
    List<Empresa> getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate fechaInicio, LocalDate fechaFin);


    // Consultas auditoria
    List<Empresa> getAllByCreadoPor(int usuarioId);
    List<Empresa> getAllByActualizadoPor(int usuarioId);

    @Procedure(name = "borrar_contenidos_empresa")
    void borrar_contenidos_empresa(int empresaId, int usuario);
}
