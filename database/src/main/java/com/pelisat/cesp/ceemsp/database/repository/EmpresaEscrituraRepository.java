package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaEscrituraRepository extends JpaRepository<EmpresaEscritura, Integer> {
    List<EmpresaEscritura> findAllByEmpresaAndEliminadoFalse(int empresa);
    EmpresaEscritura findByUuidAndEliminadoFalse(String uuid);
    List<EmpresaEscritura> findAllByNumeroEscrituraLikeAndEliminadoFalse(String numero);

    // Search
    List<EmpresaEscritura> findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombreFedatarioContaining(String apellidoPaterno, String apellidoMaterno, String nombres);
    List<EmpresaEscritura> findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombreFedatarioContainingAndEmpresa(String apellidoPaterno, String apellidoMaterno, String nombre, int empresa);
    List<EmpresaEscritura> findAllByNumeroContaining(String numero);
    List<EmpresaEscritura> findAllByNumeroContainingAndEmpresa(String numero, int empresa);
    List<EmpresaEscritura> findAllByEliminadoFalse();
}
