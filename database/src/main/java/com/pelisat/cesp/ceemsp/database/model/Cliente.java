package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CLIENTES")
@Getter
@Setter
public class Cliente extends CommonModel {
}
