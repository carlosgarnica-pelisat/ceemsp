package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardDto {
    private List<IncidenciaDto> incidenciasAbiertas;
    private List<IncidenciaDto> misIncidencias;
    private int visitasDelMesPasadas;
    private int visitasDelMesTotales;
    private int cantidadAcuerdosProximosAVencer;
    private int cantidadRequerimientosProximosAVencer;
    private int cantidadLicenciasFederalesProximasAVencer;
    private int cantidadLicenciasParticularesProximasAVencer;

    private int totalEmpresasRegistradas;

    private int empresasAutorizacionProvisional;
    private int empresasRegistroFederal;
    private int empresasAutorizacionEstatal;
    private int empresasServiciosPropios;
    private int empresasActivas;
    private int empresasPerdidaEficacia;
    private int empresasSuspendidas;
    private int empresasRevocadas;
    private int empresasClausuradas;

    private List<VisitaDto> proximasVisitas;
    private List<VisitaDto> requerimientosProximosAVencer;
    private List<AcuerdoDto> acuerdosProximosAVencer;
    private List<EmpresaEscrituraApoderadoDto> apoderadosProximosAVencer;
    private List<EmpresaDto> licenciasFederalesProximasAVencer;
    private List<EmpresaDto> empresasSinInformeMensual;
    private List<EmpresaDto> empresasConInformeMensual;
    private List<EmpresaLicenciaColectivaDto> licenciasParticularesProximasAVencer;
}
