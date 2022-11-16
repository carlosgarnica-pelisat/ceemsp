package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Arma;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArmaRepository extends JpaRepository<Arma, Integer> {
    List<Arma> getAllByEmpresaAndEliminadoFalse(int empresa);
    List<Arma> getAllByLicenciaColectivaAndEliminadoFalse(int licenciaColectiva);
    List<Arma> getAllByLicenciaColectiva(int licenciaColectiva);
    List<Arma> getAllByBunkerAndEliminadoFalse(int bunkerId);
    Arma getByUuidAndEliminadoFalse(String uuid);
    Arma getFirstBySerie(String serie);
    Arma getFirstByMatricula(String matricula);
    Integer countAllByTipoAndLicenciaColectivaAndEliminadoFalse(ArmaTipoEnum armaTipoEnum, int licencia);
}
