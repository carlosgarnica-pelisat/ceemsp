package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS_LICENCIAS_COLECTIVAS")
@Getter
@Setter
public class EmpresaLicenciaColectiva extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "NUMERO_OFICIO", nullable = false)
    private String numeroOficio;

    @Column(name = "MODALIDAD", nullable = false)
    private int modalidad;

    @Column(name = "SUBMODALIDAD", nullable = false)
    private int submodalidad;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "RUTA_DOCUMENTO", nullable = false)
    private String rutaDocumento;

    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;

    @Column(name = "OBSERVACIONES_BAJA")
    private String observacionesBaja;

    @Column(name = "DOCUMENTO_FUNDATORIO_BAJA")
    private String documentoFundatorioBaja;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;
}
