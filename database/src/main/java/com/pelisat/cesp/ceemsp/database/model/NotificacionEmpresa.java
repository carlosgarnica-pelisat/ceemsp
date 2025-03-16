package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.NotificacionArgosTipoEnum;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmpresaTipoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "NOTIFICACIONES_EMPRESAS")
@Getter
@Setter
public class NotificacionEmpresa extends CommonModel {
    @Column(name = "EMPRESA")
    private int empresa;
    @Column(name = "TIPO", nullable = false)
    private NotificacionEmpresaTipoEnum tipo;
    @Column(name = "MOTIVO", nullable = false)
    private String motivo;
    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;
    @Column(name = "LEIDO", nullable = false)
    private boolean leido;
    @Column(name = "UBICACION")
    private String ubicacion;
}
