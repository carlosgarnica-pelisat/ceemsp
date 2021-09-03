package com.pelisat.cesp.ceemsp.database.model;

import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class CommonModel {

    private static final Integer MAXIMUM_UUID_CHARS = 12;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(name = "UUID", nullable = false, updatable = false)
    protected String uuid;

    @Column(name = "ELIMINADO", nullable = false)
    protected Boolean isDeleted;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    protected LocalDateTime fechaCreacion;

    @Column(name = "CREADO_POR", nullable = false, updatable = false)
    protected Integer creadoPor;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    protected LocalDateTime fechaActualizacion;

    @Column(name = "ACTUALIZADO_POR", nullable = false)
    protected Integer actualizadoPor;

    public CommonModel() {
        this.fechaActualizacion = LocalDateTime.now();
        if(this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        this.uuid = RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS);
    }
}
