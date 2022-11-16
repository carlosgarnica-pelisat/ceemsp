package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoDestinatarioEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuzonInternoDestinatarioDto {
    private int id;
    private String uuid;
    private TipoDestinatarioEnum tipoDestinatario;
    private String email;
    private UsuarioDto usuario;
    private EmpresaDto empresa;
    private boolean visto;
    private String fechaVisto;
    private String fechaActualizacion;
}
