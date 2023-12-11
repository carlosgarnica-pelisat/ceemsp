package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EMPRESAS_REPORTES_MENSUALES")
@Getter
@Setter
public class EmpresaReporteMensual extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "PERSONAL_ACTIVOS", nullable = false)
    private int personalActivos;

    @Column(name = "PERSONAL_ALTAS", nullable = false)
    private int personalAltas;

    @Column(name = "PERSONAL_BAJAS", nullable = false)
    private int personalBajas;

    @Column(name = "PERSONAL_TOTAL", nullable = false)
    private int personalTotal;

    @Column(name = "CLIENTES_ACTIVOS", nullable = false)
    private int clientesActivos;

    @Column(name = "CLIENTES_ALTAS", nullable = false)
    private int clientesAltas;

    @Column(name = "CLIENTES_BAJAS", nullable = false)
    private int clientesBajas;

    @Column(name = "CLIENTES_TOTAL", nullable = false)
    private int clientesTotal;

    @Column(name = "VEHICULOS_ACTIVOS", nullable = false)
    private int vehiculosActivos;

    @Column(name = "VEHICULOS_ALTAS", nullable = false)
    private int vehiculosAltas;

    @Column(name = "VEHICULOS_BAJAS", nullable = false)
    private int vehiculosBajas;

    @Column(name = "VEHICULOS_TOTAL", nullable = false)
    private int vehiculosTotal;

    @Column(name = "EQUIPO_ACTIVOS", nullable = false)
    private int equipoActivos;

    @Column(name = "EQUIPO_ALTAS", nullable = false)
    private int equipoAltas;

    @Column(name = "EQUIPO_BAJAS", nullable = false)
    private int equipoBajas;

    @Column(name = "EQUIPO_TOTAL", nullable = false)
    private int equipoTotal;

    @Column(name = "UNIFORMES_ACTIVOS", nullable = false)
    private int uniformesActivos;

    @Column(name = "UNIFORMES_ALTAS", nullable = false)
    private int uniformesAltas;

    @Column(name = "UNIFORMES_BAJAS", nullable = false)
    private int uniformesBajas;

    @Column(name = "UNIFORMES_TOTAL", nullable = false)
    private int uniformesTotal;

    @Column(name = "INCIDENCIAS_REPORTADAS", nullable = false)
    private int incidenciasReportadas;

    @Column(name = "INCIDENCIAS_TOTAL", nullable = false)
    private int incidenciasTotal;

    @Column(name = "CANES_ASIGNADOS")
    private Integer canesAsignados;

    @Column(name = "CANES_INSTALACIONES")
    private Integer canesInstalaciones;

    @Column(name = "CANES_ALTAS")
    private Integer canesAltas;

    @Column(name = "CANES_BAJAS")
    private Integer canesBajas;

    @Column(name = "CANES_TOTAL")
    private Integer canesTotal;

    @Column(name = "ARMAS_1_ALTAS")
    private Integer armas1Altas;

    @Column(name = "ARMAS_2_ALTAS")
    private Integer armas2Altas;

    @Column(name = "ARMAS_3_ALTAS")
    private Integer armas3Altas;

    @Column(name = "ARMAS_1_BAJAS")
    private Integer armas1Bajas;

    @Column(name = "ARMAS_2_BAJAS")
    private Integer armas2Bajas;

    @Column(name = "ARMAS_3_BAJAS")
    private Integer armas3Bajas;

    @Column(name = "ARMAS_1_ACTIVAS")
    private Integer armas1Activas;

    @Column(name = "ARMAS_2_ACTIVAS")
    private Integer armas2Activas;

    @Column(name = "ARMAS_3_ACTIVAS")
    private Integer armas3Activas;

    @Column(name = "ARMAS_1_TOTAL")
    private Integer armas1Total;

    @Column(name = "ARMAS_2_TOTAL")
    private Integer armas2Total;

    @Column(name = "ARMAS_3_TOTAL")
    private Integer armas3Total;

    @Column(name = "REPORTA_UNIFORMES")
    private Boolean reportaUniformes;

    @Column(name = "INCIDENCIAS_PROCEDENTES")
    private Integer incidenciasProcedentes;

    @Column(name = "INCIDENCIAS_IMPROCEDENTES")
    private Integer incidenciasImprocedentes;

    @Column(name = "CADENA_ORIGINAL")
    private String cadenaOriginal;

    @Column(name = "SELLO")
    private String sello;

    @Column(name = "SELLO_SALT")
    private String selloSalt;

    @Column(name = "VENTANA")
    private Integer ventana;

    @Column(name = "NUMERO")
    private String numero;
}
