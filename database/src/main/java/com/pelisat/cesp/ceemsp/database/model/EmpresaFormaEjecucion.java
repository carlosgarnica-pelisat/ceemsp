package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "EMPRESAS_FORMAS_EJECUCION")
@Getter
@Setter
public class EmpresaFormaEjecucion extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private int empresa;

    @Column(name = "FORMA_EJECUCION", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormaEjecucionEnum formaEjecucion;

    public EmpresaFormaEjecucion() {
        super();
    }
}
