package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "EMPRESAS_DOMICILIOS")
public class EmpresaDomicilio extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DOMICILIO_1", nullable = false)
    private String domicilio1;

    @Column(name = "NUMERO_EXTERIOR", nullable = false)
    private String numeroExterior;

    @Column(name = "NUMERO_INTERIOR")
    private String numeroInterior;

    @Column(name = "DOMICILIO_2", nullable = false)
    private String domicilio2;

    @Column(name = "DOMICILIO_3", nullable = false)
    private String domicilio3;

    @Column(name = "DOMICILIO_4")
    private String domicilio4;

    @Column(name = "CODIGO_POSTAL", nullable = false)
    private String codigoPostal;

    @Column(name = "ESTADO", nullable = false)
    private String estado;

    @Column(name = "PAIS", nullable = false)
    private String pais;

    @Column(name = "FOTO_FACHADA", nullable = false)
    private String fotoFachada;

    @Column(name = "MATRIZ", nullable = false)
    private boolean matriz;

    @Column(name = "TELEFONO_FIJO", nullable = false)
    private String telefonoFijo;

    @Column(name = "TELEFONO_MOVIL", nullable = false)
    private String telefonoMovil;

    @Column(name = "LATITUD", nullable = false)
    private String latitud;

    @Column(name = "LONGITUD", nullable = false)
    private String longitud;
}
