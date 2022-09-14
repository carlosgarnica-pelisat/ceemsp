package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CLIENTES_ASIGNACIONES_PERSONAL")
@Getter
@Setter
public class ClienteAsignacionPersonal extends CommonModel {
    @Column(name = "CLIENTE", nullable = false)
    private int cliente;

    @Column(name = "CLIENTE_DOMICILIO", nullable = false)
    private int clienteDomicilio;

    @Column(name = "PERSONAL", nullable = false)
    private int personal;
}
