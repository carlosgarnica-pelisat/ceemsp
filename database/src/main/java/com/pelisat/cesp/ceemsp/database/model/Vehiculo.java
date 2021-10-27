package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "VEHICULOS")
@Getter
@Setter
public class Vehiculo extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "TIPO", nullable = false)
    private int tipo;

    @Column(name = "MARCA", nullable = false)
    private int marca;

    @Column(name = "SUBMARCA", nullable = false)
    private int submarca;

    @Column(name = "ANIO", nullable = false)
    private String anio;

    @Column(name = "COLOR", nullable = false)
    private String color;

    @Column(name = "ROTULADO")
    private boolean rotulado;

    @Column(name = "PLACAS", nullable = false)
    private String placas;

    @Column(name = "SERIE", nullable = false)
    private String serie;

    @Column(name = "USERNAME", nullable = false)
    private String origen;

    @Column(name = "BLINDADO")
    private boolean blindado;

    @Column(name = "SERIE_BLINDAJE")
    private String serieBlindaje;

    @Column(name = "FECHA_BLINDAJE")
    private LocalDate fechaBlindaje;

    @Column(name = "NUMERO_HOLOGRAMA")
    private String numeroHolograma;

    @Column(name = "PLACA_METALICA")
    private String placaMetalica;

    @Column(name = "EMPRESA_BLINDAJE")
    private String empresaBlindaje;

    @Column(name = "NIVEL_BLINDAJE")
    private String nivelBlindaje;

    public Vehiculo() {
        super();
    }
}
