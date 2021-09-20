package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoPersonaEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
}
