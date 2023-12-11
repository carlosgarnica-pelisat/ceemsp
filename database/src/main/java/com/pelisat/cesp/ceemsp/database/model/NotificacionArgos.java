package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.NotificacionArgosTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "NOTIFICACIONES_ARGOS")
@Getter
@Setter
public class NotificacionArgos extends CommonModel {
    @Column(name = "USUARIO")
    private int usuario;
    @Column(name = "TIPO", nullable = false)
    private NotificacionArgosTipoEnum tipo;
    @Column(name = "MOTIVO", nullable = false)
    private String motivo;
    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;
    @Column(name = "LEIDO", nullable = false)
    private boolean leido;
    @Column(name = "UBICACION")
    private String ubicacion;
}
