package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IncidenciaDto {
    private String numero;
    private String fechaIncidencia;
    private boolean relevancia;
    private ClienteDto cliente;
    private String latitud;
    private String longitud;
    private IncidenciaStatusEnum status;
    private UsuarioDto asignado;

    //TODO: Revisar si es mejor crear dtos para las tablas intermedias o dejarlos asi
    private List<CanDto> canesInvolucrados;
    private List<ArmaDto> armasInvolucradas;
    private List<IncidenciaComentarioDto> comentarios;
    private List<PersonaDto> personasInvolucradas;
    private List<VehiculoDto> vehiculosInvolucrados;
}
