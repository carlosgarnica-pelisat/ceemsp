package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CLIENTES_DOMICILIOS")
@Getter
@Setter
public class ClienteDomicilio extends CommonModel {
    @Column(name = "CLIENTE", nullable = false)
    private int cliente;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DOMICILIO_1", nullable = false)
    private String domicilio1;

    @Column(name = "NUMERO_EXTERIOR", nullable = false)
    private String numeroExterior;

    @Column(name = "NUMERO_INTERIOR")
    private String numeroInterior;

    @Column(name = "LOCALIDAD", nullable = false)
    private String localidad;

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

    @Column(name = "MATRIZ", nullable = false)
    private boolean matriz;

    @Column(name = "CONTACTO", nullable = false)
    private String contacto;

    @Column(name = "TELEFONO_FIJO", nullable = false)
    private String telefonoFijo;

    @Column(name = "TELEFONO_MOVIL", nullable = false)
    private String telefonoMovil;

    @Column(name = "CORREO_ELECTRONICO", nullable = false)
    private String correoElectronico;

    @Column(name = "TIPO_INFRAESTRUCTURA", nullable = false)
    private int tipoInfraestructura;

    @Column(name = "TIPO_INFRAESTRUCTURA_OTRO")
    private String tipoInfraestructuraOtro;

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
}
