package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.TipoFedatarioEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS_ESCRITURAS")
@Getter
@Setter
public class EmpresaEscritura extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "NUMERO_ESCRITURA", nullable = false)
    private String numeroEscritura;

    @Column(name = "FECHA_ESCRITURA", nullable = false)
    private LocalDate fechaEscritura;

    @Column(name = "CIUDAD", nullable = false)
    private String ciudad;

    @Column(name = "TIPO_FEDATARIO", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoFedatarioEnum tipoFedatario;

    @Column(name = "NUMERO", nullable = false)
    private String numero;

    @Column(name = "NOMBRE_FEDATARIO", nullable = false)
    private String nombreFedatario;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "RUTA_ARCHIVO", nullable = false)
    private String rutaArchivo;

    @Column(name = "CURP")
    private String curp;

    @Column(name = "APELLIDO_PATERNO")
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO")
    private String apellidoMaterno;

    @Column(name = "ESTADO_CATALOGO", nullable = false)
    private int estadoCatalogo;

    @Column(name = "MUNICIPIO_CATALOGO", nullable = false)
    private int municipioCatalogo;

    @Column(name = "LOCALIDAD_CATALOGO", nullable = false)
    private int localidadCatalogo;
}
