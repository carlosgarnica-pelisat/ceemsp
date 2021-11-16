package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "EMPRESAS_MODALIDADES")
@Getter
@Setter
public class EmpresaModalidad extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "MODALIDAD", nullable = false)
    private int modalidad;

    @Column(name = "SUBMODALIDAD")
    private Integer submodalidad;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "NUMERO_REGISTRO_FEDERAL")
    private String numeroRegistroFederal;
}
