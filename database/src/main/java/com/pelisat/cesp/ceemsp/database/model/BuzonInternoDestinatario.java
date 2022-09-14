package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.TipoDestinatarioEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "BUZON_INTERNO_DESTINATARIOS")
public class BuzonInternoDestinatario extends CommonModel {
    @Column(name = "BUZON_INTERNO", nullable = false)
    private int buzonInterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DESTINATARIO", nullable = false)
    private TipoDestinatarioEnum tipoDestinatario;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "USUARIO")
    private Integer usuario;

    @Column(name = "EMPRESA")
    private Integer empresa;
}
