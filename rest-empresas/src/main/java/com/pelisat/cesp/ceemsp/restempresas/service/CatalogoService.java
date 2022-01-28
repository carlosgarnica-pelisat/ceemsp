package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.ArmaMarca;
import com.pelisat.cesp.ceemsp.database.model.PersonalNacionalidad;

import java.util.List;

public interface CatalogoService {
    List<CanRazaDto> obtenerCanesRazas();
    List<CanTipoAdiestramientoDto> obtenerCanesAdiestramientos();

    List<ArmaClaseDto> obtenerClasesArmas();
    List<ArmaMarcaDto> obtenerArmasMarcas();
    List<ArmaTipoDto> obtenerArmasTipos();

    List<ModalidadDto> obtenerModalidades();

    List<PersonalNacionalidadDto> obtenerNacionalidades();
    List<PersonalPuestoDeTrabajoDto> obtenerPuestosDeTrabajo();

    List<TipoInfraestructuraDto> obtenerTiposInfraestructura();

    List<UniformeDto> obtenerUniformes();

    List<VehiculoUsoDto> obtenerUsosVehiculos();
    List<VehiculoMarcaDto> obtenerMarcasVehiculos();
}
