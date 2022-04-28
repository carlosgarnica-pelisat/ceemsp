package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.TipoPersonaEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CLIENTES")
@Getter
@Setter
public class Cliente extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "TIPO_PERSONA", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPersonaEnum tipoPersona;

    @Column(name = "RFC", nullable = false)
    private String rfc;

    @Column(name = "NOMBRE_COMERCIAL", nullable = false)
    private String nombreComercial;

    @Column(name = "RAZON_SOCIAL", nullable = false)
    private String razonSocial;

    @Column(name = "CANES")
    private boolean canes;

    @Column(name = "ARMAS")
    private boolean armas;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "RUTA_ARCHIVO_CONTRATO")
    private String rutaArchivoContrato;

    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;

    @Column(name = "OBSERVACIONES_BAJA")
    private String observacionesBaja;

    @Column(name = "DOCUMENTO_FUNDATORIO_BAJA")
    private String documentoFundatorioBaja;
}
