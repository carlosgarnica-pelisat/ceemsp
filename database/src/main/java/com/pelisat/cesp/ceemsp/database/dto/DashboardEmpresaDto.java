package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class   DashboardEmpresaDto {
    private int incidenciasPendientes;
    private int incidenciasAbiertas;
    private int incidenciasProcedentes;
    private int incidenciasImprocedentes;
    private int incidenciasContestadas;
    private int acuerdosProximosAVencer;
    private int acuerdosTotales;
    private int requerimientosProximosAVencer;
    private int personalActivo;
    private long diasRestantesLicenciaFederal;
    private int licenciasColectivasProximasAVencer;
    private int clientesProximosAVencer;
    private List<VisitaDto> requerimientosProximos;

    private List<VisitaDto> proximasVisitas;
    private List<VisitaDto> listaRequerimientosProximosAVencer;
    private List<AcuerdoDto> listaAcuerdosProximosAVencer;
}
