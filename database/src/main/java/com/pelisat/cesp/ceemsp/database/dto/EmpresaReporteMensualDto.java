package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class EmpresaReporteMensualDto {
    private int id;
    private String uuid;
    private int personalActivos;
    private int personalAltas;
    private int personalBajas;
    private int personalTotal;
    private int clientesActivos;
    private int clientesAltas;
    private int clientesBajas;
    private int clientesTotal;
    private int vehiculosActivos;
    private int vehiculosAltas;
    private int vehiculosBajas;
    private int vehiculosTotal;
    private int equipoActivos;
    private int equipoAltas;
    private int equipoBajas;
    private int equipoTotal;
    private int uniformesActivos;
    private int uniformesAltas;
    private int uniformesBajas;
    private int uniformesTotal;
    private int incidenciasReportadas;
    private int incidenciasTotal;
    private Integer canesAsignados;
    private Integer canesInstalaciones;
    private Integer canesAltas;
    private Integer canesBajas;
    private Integer canesTotal;
    private Integer armas1Altas;
    private Integer armas2Altas;
    private Integer armas3Altas;
    private Integer armas1Bajas;
    private Integer armas2Bajas;
    private Integer armas3Bajas;
    private Integer armas1Activas;
    private Integer armas2Activas;
    private Integer armas3Activas;
    private Integer armas1Total;
    private Integer armas2Total;
    private Integer armas3Total;
    private Boolean reportaUniformes;
    private Integer incidenciasProcedentes;
    private Integer incidenciasImprocedentes;
    private String cadenaOriginal;
    private String sello;
    private String fechaCreacion;
    private String numero;
}
