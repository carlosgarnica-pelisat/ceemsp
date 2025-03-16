package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoPersonaEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS")
@Getter
@Setter
public class Empresa extends CommonModel {
    @Column(name = "RAZON_SOCIAL", nullable = false)
    private String razonSocial;

    @Column(name = "NOMBRE_COMERCIAL", nullable = false)
    private String nombreComercial;

    @Column(name = "TIPO_TRAMITE", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTramiteEnum tipoTramite;

    @Column(name = "REGISTRO", nullable = false)
    private String registro;

    @Column(name = "TIPO_PERSONA", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPersonaEnum tipoPersona;

    @Column(name = "RFC", nullable = false)
    private String rfc;

    @Column(name = "CURP")
    private String curp;

    @Column(name = "SEXO")
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

    @Column(name = "CORREO_ELECTRONICO", nullable = false)
    private String correoElectronico;

    @Column(name = "TELEFONO", nullable = false)
    private String telefono;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmpresaStatusEnum status;

    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "REGISTRO_FEDERAL")
    private String registroFederal;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "RUTA_LOGO")
    private String rutaLogo;

    @Column(name = "RUTA_REGISTRO_FEDERAL")
    private String rutaRegistroFederal;

    @Column(name = "DOMICILIOS_CAPTURADOS")
    private boolean domiciliosCapturados;

    @Column(name = "ESCRITURAS_CAPTURADAS")
    private boolean escriturasCapturadas;

    @Column(name = "FORMAS_EJECUCION_CAPTURADAS")
    private boolean formasEjecucionCapturadas;

    @Column(name = "ACUERDOS_CAPTURADOS")
    private boolean acuerdosCapturados;

    @Column(name = "VIGENCIA_INICIO")
    private LocalDate vigenciaInicio;

    @Column(name = "VIGENCIA_FIN")
    private LocalDate vigenciaFin;
}
