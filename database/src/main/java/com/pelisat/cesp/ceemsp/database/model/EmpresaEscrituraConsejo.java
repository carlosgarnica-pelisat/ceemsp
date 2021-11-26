package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS_ESCRITURAS_CONSEJOS")
@Getter
@Setter
public class EmpresaEscrituraConsejo extends CommonModel {
    @Column(name = "ESCRITURA", nullable = false)
    private int escritura;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false)
    private String apellidos;

    @Column(name = "SEXO", nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

    @Column(name = "PUESTO", nullable = false)
    private String puesto;
}
