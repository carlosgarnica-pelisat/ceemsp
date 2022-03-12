package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.CuipStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.EstadoCivilEnum;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoSangreEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "PERSONAL")
public class Personal extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "NACIONALIDAD", nullable = false)
    private int nacionalidad;

    @Column(name = "CURP")
    private String curp;

    @Column(name = "APELLIDO_PATERNO", nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false)
    private String apellidoMaterno;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "FECHA_NACIMIENTO", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "SEXO", nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

    @Column(name = "TIPO_SANGRE", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoSangreEnum tipoSangre;

    @Column(name = "FECHA_INGRESO", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "ESTADO_CIVIL", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCivilEnum estadoCivil;

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

    @Column(name = "ESTADO", nullable = false)
    private String estado;

    @Column(name = "PAIS", nullable = false)
    private String pais;

    @Column(name = "CODIGO_POSTAL", nullable = false)
    private String codigoPostal;

    @Column(name = "TELEFONO", nullable = false)
    private String telefono;

    @Column(name = "CORREO_ELECTRONICO", nullable = false)
    private String correoElectronico;

    @Column(name = "PUESTO", nullable = false)
    private int puesto;

    @Column(name = "SUBPUESTO", nullable = false)
    private int subpuesto;

    @Column(name = "DETALLES_PUESTO", nullable = false)
    private String detallesPuesto;

    @Column(name = "DOMICILIO_ASIGNADO")
    private int domicilioAsignado;

    @Column(name = "ESTATUS_CUIP")
    @Enumerated(EnumType.STRING)
    private CuipStatusEnum estatusCuip;

    @Column(name = "CUIP")
    private String cuip;

    @Column(name = "NUMERO_VOLANTE_CUIP")
    private String numeroVolanteCuip;

    @Column(name = "FECHA_VOLANTE_CUIP")
    private LocalDate fechaVolanteCuip;

    @Column(name = "MODALIDAD")
    private int modalidad;

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

