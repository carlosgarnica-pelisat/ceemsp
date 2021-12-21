package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "PERSONAL_CERTIFICACIONES")
public class PersonalCertificacion extends CommonModel {
    @Column(name = "PERSONAL", nullable = false)
    private int personal;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "NOMBRE_INSTRUCTOR", nullable = false)
    private String nombreInstructor;

    @Column(name = "DURACION", nullable = false)
    private BigDecimal duracion;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;
}
