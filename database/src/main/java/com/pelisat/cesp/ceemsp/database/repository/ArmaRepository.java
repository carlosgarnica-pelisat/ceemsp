package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Arma;
import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ArmaRepository extends JpaRepository<Arma, Integer> {
    List<Arma> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Arma> getAllByEmpresaAndTipoAndStatusAndEliminadoFalse(int empresa, ArmaTipoEnum tipo, ArmaStatusEnum status);
    List<Arma> getAllByEmpresaAndStatusAndEliminadoFalse(int empresa, ArmaStatusEnum armaStatusEnum);
    List<Arma> getAllByLicenciaColectivaAndEliminadoFalse(int licenciaColectiva);
    List<Arma> getAllByLicenciaColectiva(int licenciaColectiva);
    List<Arma> getAllByBunkerAndEliminadoFalse(int bunkerId);
    Arma getByUuidAndEliminadoFalse(String uuid);
    Arma getByUuid(String uuid);
    Arma getFirstBySerieAndEliminadoFalse(String serie);
    Arma getFirstByMatriculaAndEliminadoFalse(String matricula);
    Arma getFirstByMatricula(String matricula);
    Integer countAllByTipoAndLicenciaColectivaAndEliminadoFalse(ArmaTipoEnum armaTipoEnum, int licencia);
    Integer countByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(int licenciaColectiva, LocalDateTime finMes, LocalDateTime inicioMes);
    Integer countByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(int licenciaColectiva, LocalDateTime finMes, LocalDateTime inicioMes);
    Integer countByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(int licenciaColectiva, LocalDateTime finMes);

    // Search
    List<Arma> findAllByMatriculaContaining(String matricula);
    List<Arma> findAllByMatriculaContainingAndEmpresa(String matricula, int empresa);
    List<Arma> findAllBySerieContaining(String serie);
    List<Arma> findAllBySerieContainingAndEmpresa(String serie, int empresa);
    List<Arma> findAllByEliminadoFalse();
}
