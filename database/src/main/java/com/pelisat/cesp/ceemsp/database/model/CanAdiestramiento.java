package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "CANES_ADIESTRAMIENTOS")
@Getter
@Setter
public class CanAdiestramiento extends CommonModel {
    //TODO: Convertir a relationship de hibernate
    @Column(name = "CAN", nullable = false)
    private Integer can;

    @Column(name = "NOMBRE_INSTRUCTOR", nullable = false)
    private String nombreInstructor;

    @Column(name = "TIPO_ADIESTRAMIENTO", nullable = false)
    private Integer tipoAdiestramiento;

    @Column(name = "FECHA_CONSTANCIA", nullable = false)
    private LocalDate fechaConstancia;
}
