package com.pelisat.cesp.ceemsp.database.model;


import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "VISITAS")
@Getter
@Setter
public class Visita extends CommonModel {
    @Column(name = "EMPRESA")
    private Integer empresa;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoVisitaEnum tipoVisita;

    @Column(name = "NUMERO_REGISTRO")
    private String numeroRegistro;

    @Column(name = "NUMERO_ORDEN", nullable = false)
    private String numeroOrden;

    @Column(name = "FECHA_VISITA", nullable = false)
    private LocalDate fechaVisita;

    @Column(name = "REQUERIMIENTO", nullable = false)
    private boolean requerimiento;

    @Column(name = "DETALLES_REQUERIMIENTO")
    private String detallesRequerimiento;

    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "FECHA_TERMINO")
    private LocalDate fechaTermino;

    @Column(name = "RESPONSABLE", nullable = false)
    private int responsable;

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

    @Column(name = "RAZON_SOCIAL", nullable = false)
    private String razonSocial;

    @Column(name = "NOMBRE_COMERCIAL", nullable = false)
    private String nombreComercial;

    @Column(name = "LOCALIDAD", nullable = false)
    private String localidad;

    @Column(name = "ESTADO_CATALOGO", nullable = false)
    private int estadoCatalogo;

    @Column(name = "CALLE_CATALOGO", nullable = false)
    private int calleCatalogo;

    @Column(name = "COLONIA_CATALOGO", nullable = false)
    private int coloniaCatalogo;

    @Column(name = "MUNICIPIO_CATALOGO", nullable = false)
    private int municipioCatalogo;

    @Column(name = "LOCALIDAD_CATALOGO", nullable = false)
    private int localidadCatalogo;

    @Column(name = "EMPRESA_DOMICILIO", nullable = false)
    private Integer empresaDomicilio;
}
