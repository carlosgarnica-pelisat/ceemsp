package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.dto.ConteoMensualDto;
import com.pelisat.cesp.ceemsp.database.model.EmpresaReporteMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmpresaReporteMensualRepository extends JpaRepository<EmpresaReporteMensual, Integer> {
    List<EmpresaReporteMensual> getAllByEmpresaAndEliminadoFalse(int empresa);
    EmpresaReporteMensual getByUuidAndEliminadoFalse(String uuid);
    List<EmpresaReporteMensual> getAllByVentanaAndEliminadoFalse(Integer ventana);
    List<EmpresaReporteMensual> getAllByEmpresaAndVentanaAndEliminadoFalse(Integer empresa, Integer ventana);
    EmpresaReporteMensual findBySelloAndEliminadoFalse(String sello);

    EmpresaReporteMensual findFirstByEliminadoFalseOrderByFechaCreacionDesc();

    List<EmpresaReporteMensual> findByFechaCreacionGreaterThanAndFechaCreacionLessThanAndEliminadoFalse(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.pelisat.cesp.ceemsp.database.dto.ConteoMensualDto(SUM(e.personalActivos), SUM(e.personalAltas), SUM(e.personalBajas), SUM(e.personalTotal), " +
            "SUM(e.clientesActivos), SUM(e.clientesAltas), SUM(e.clientesBajas), SUM(e.clientesTotal)," +
            "SUM(e.vehiculosActivos), SUM(e.vehiculosAltas), SUM(e.vehiculosBajas), SUM(e.vehiculosTotal)," +
            "SUM(e.equipoActivos), SUM(e.equipoAltas), SUM(e.equipoBajas), SUM(e.equipoTotal)," +
            "SUM(e.canesAsignados), SUM(e.canesInstalaciones), SUM(e.canesAltas), SUM(e.canesBajas), SUM(e.canesTotal)," +
            "SUM(e.armas1Activas), SUM(e.armas1Altas), SUM(e.armas1Bajas), SUM(e.armas1Total)," +
            "SUM(e.armas2Activas), SUM(e.armas2Altas), SUM(e.armas2Bajas), SUM(e.armas2Total)," +
            "SUM(e.armas3Activas), SUM(e.armas3Altas), SUM(e.armas3Bajas), SUM(e.armas3Total))" +
            "FROM EmpresaReporteMensual as e " +
            "WHERE e.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY FUNCTION('date_format', e.fechaCreacion, '%Y, %m')")
    List<ConteoMensualDto> getSumReportesMensualesByMonthAndYear(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
