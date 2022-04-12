package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardDto {
    private int incidenciasAbiertas;
    private int visitasDelMesPasadas;
    private int visitasDelMesTotales;

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
}
