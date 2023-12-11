package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.dto.metadata.IncidenciaArchivoMetadata;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IncidenciaDto {
    private int id;
    private String uuid;
    private String numero;
    private String fechaIncidencia;
    private boolean relevancia = true; //TODO: Remover esto, ya no se necesita
    private ClienteDto cliente;
    private String latitud;
    private String longitud;
    private IncidenciaStatusEnum status;
    private UsuarioDto asignado;
    private String fechaCreacion;
    private String fechaActualizacion;
    private boolean eliminado;

    private List<CanDto> canesInvolucrados;
    private List<ArmaDto> armasInvolucradas;
    private List<IncidenciaComentarioDto> comentarios;
    private List<PersonaDto> personasInvolucradas;
    private List<VehiculoDto> vehiculosInvolucrados;
    private List<IncidenciaArchivoMetadata> archivos;

    private ClienteDomicilioDto clienteDomicilio;
    private EmpresaDto empresa;
}
