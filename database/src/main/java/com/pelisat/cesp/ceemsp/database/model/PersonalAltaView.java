package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "personal_altas_view")
@Immutable
@Getter
@Setter
public class PersonalAltaView {
    @Column(name = "id")
    @Id
    private int id;

    @Column(name = "empresa")
    private int empresa;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
