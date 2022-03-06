package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "ESTADOS")
public class Estado extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;
}
