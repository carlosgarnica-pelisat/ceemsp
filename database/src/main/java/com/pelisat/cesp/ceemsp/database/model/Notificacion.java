package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "NOTIFICACIONES")
@Getter
@Setter
public class Notificacion extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "NUMERO", nullable = false)
    private String numero;

    @Column(name = "TIPO", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificacionEmailEnum tipo;

    @Column(name = "CADENA_ORIGINAL", nullable = false)
    private String cadenaOriginal;

    @Column(name = "SELLO", nullable = false)
    private String sello;

    @Column(name = "SELLO_SALT", nullable = false)
    private String selloSalt;
}
