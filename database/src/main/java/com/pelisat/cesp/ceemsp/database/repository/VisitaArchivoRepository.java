package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalFotografia;
import com.pelisat.cesp.ceemsp.database.model.VisitaArchivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitaArchivoRepository extends JpaRepository<VisitaArchivo, Integer> {
    List<VisitaArchivo> getAllByVisitaAndEliminadoFalse(int visitaId);
    VisitaArchivo getByUuidAndEliminadoFalse(String uuid);
}
