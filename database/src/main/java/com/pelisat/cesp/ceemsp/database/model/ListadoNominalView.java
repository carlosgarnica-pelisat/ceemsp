package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "listado_nominal_view")
@Immutable
@Getter
@Setter
public class ListadoNominalView {

    @Column(name = "id")
    @Id
    private int id;

    @Column(name = "cuip")
    private String cuip;

    @Column(name = "rfc")
    private String rfc;

    @Column(name = "curp")
    private String curp;

    @Column(name = "apellido_paterno")
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "fecha_ingreso")
    private String fechaIngreso;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "modalidad")
    private String modalidad;

    @Column(name = "registro")
    private String registro;
}
