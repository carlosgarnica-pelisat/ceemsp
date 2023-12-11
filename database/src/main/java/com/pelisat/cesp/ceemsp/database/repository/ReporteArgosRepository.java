package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ReporteArgos;
import com.pelisat.cesp.ceemsp.database.type.ReporteArgosStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteArgosRepository extends JpaRepository<ReporteArgos, Integer> {
    List<ReporteArgos> getAllByStatusAndEliminadoFalse(ReporteArgosStatusEnum reporteArgosStatusEnum);
    ReporteArgos findByUuidAndEliminadoFalse(String uuid);
}
