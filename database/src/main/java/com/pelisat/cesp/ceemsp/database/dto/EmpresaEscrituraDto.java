package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoFedatarioEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmpresaEscrituraDto {
    private int id;
    private String uuid;
    private String numeroEscritura;
    private String fechaEscritura;
    private String ciudad;
    private TipoFedatarioEnum tipoFedatario;
    private String numero;
    private String nombreFedatario;
    private String descripcion;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String curp;
    private EstadoDto estadoCatalogo;
    private MunicipioDto municipioCatalogo;
    private LocalidadDto localidadCatalogo;

    private List<EmpresaEscrituraSocioDto> socios;
    private List<EmpresaEscrituraApoderadoDto> apoderados;
    private List<EmpresaEscrituraRepresentanteDto> representantes;
    private List<EmpresaEscrituraConsejoDto> consejos;
}
