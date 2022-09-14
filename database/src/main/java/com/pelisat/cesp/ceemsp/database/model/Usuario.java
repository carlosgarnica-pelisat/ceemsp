package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.type.RolTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "USUARIOS")
@Getter
@Setter
public class Usuario extends CommonModel {
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false)
    private String apellidos;

    @Column(name = "APELLIDO_MATERNO")
    private String apellidoMaterno;

    @Column(name = "EMPRESA")
    private Integer empresa;

    @Column(name = "ROL")
    @Enumerated(EnumType.STRING)
    RolTypeEnum rol;

    public Usuario() {
        super();
    }
}
