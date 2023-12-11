package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "CLIENTES_FORMAS_EJECUCION")
@Getter
@Setter
public class ClienteFormaEjecucion extends CommonModel {
    @Column(name = "CLIENTE", nullable = false)
    private int cliente;

    @Column(name = "FORMA_EJECUCION", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormaEjecucionEnum formaEjecucion;
}
