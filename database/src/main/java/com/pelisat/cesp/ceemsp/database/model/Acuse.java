package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.AcuseTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "ACUSES")
public class Acuse extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "NUMERO", nullable = false)
    private String numero;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private AcuseTipoEnum tipo;

    @Column(name = "CADENA_ORIGINAL", nullable = false)
    private String cadenaOriginal;

    @Column(name = "SELLO", nullable = false)
    private String sello;

    @Column(name = "INCIDENCIA")
    private Integer incidencia;

    @Column(name = "SELLO_SALT")
    private String selloSalt;
}
