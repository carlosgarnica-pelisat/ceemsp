package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CLIENTE_MODALIDADES")
@Getter
@Setter
public class ClienteModalidad extends CommonModel {
    @Column(name = "CLIENTE", nullable = false)
    private int cliente;

    @Column(name = "EMPRESA_MODALIDAD", nullable = false)
    private int empresaModalidad;
}
