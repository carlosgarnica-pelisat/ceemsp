package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "INCIDENCIAS")
@Getter
@Setter
public class Incidencia extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "NUMERO", nullable = false)
    private String numero;

    @Column(name = "FECHA_INCIDENCIA", nullable = false)
    private LocalDate fechaIncidencia;

    @Column(name = "RELEVANCIA", nullable = false)
    private boolean relevancia;

    @Column(name = "CLIENTE")
    private Integer cliente;

    @Column(name = "LATITUD")
    private String latitud;

    @Column(name = "LONGITUD")
    private String longitud;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private IncidenciaStatusEnum status;

    @Column(name = "ASIGNADO")
    private Integer asignado;
}
