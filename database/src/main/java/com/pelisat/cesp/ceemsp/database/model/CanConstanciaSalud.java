package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "CANES_CONSTANCIAS_SALUD")
@Getter
@Setter
public class CanConstanciaSalud extends CommonModel {
    //TODO: Convertir a relationship de hibernate
    @Column(name = "CAN", nullable = false)
    private Integer can;

    @Column(name = "EXPEDIDO_POR", nullable = false)
    private String expedidoPor;

    @Column(name = "CEDULA", nullable = false)
    private String cedula;

    @Column(name = "FECHA_EXPEDICION", nullable = false)
    private LocalDate fechaExpedicion;

    @Column(name = "RUTA_DOCUMENTO", nullable = false)
    private String rutaDocumento;
}
