package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS_ESCRITURAS_APODERADOS")
@Getter
@Setter
public class EmpresaEscrituraApoderado extends CommonModel {
    @Column(name = "ESCRITURA", nullable = false)
    private int escritura;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false)
    private String apellidos;

    @Column(name = "APELLIDO_MATERNO")
    private String apellidoMaterno;

    @Column(name = "SEXO", nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "CURP")
    private String curp;
}
