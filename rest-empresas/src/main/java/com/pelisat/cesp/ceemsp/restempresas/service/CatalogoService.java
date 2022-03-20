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

    List<CalleDto> obtenerCalles(Integer limit);
    List<CalleDto> obtenerCallesPorQuery(String query);

    List<EstadoDto> obtenerTodosLosEstados();
    List<MunicipioDto> obtenerMunicipiosPorEstadoUuid(String uuidEstado);
    List<LocalidadDto> obtenerLocalidadesPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid);
    List<ColoniaDto> obtenerColoniasPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid);

    EstadoDto obtenerEstadoPorId(int id);
    MunicipioDto obtenerMunicipioPorId(int id);
    LocalidadDto obtenerLocalidadPorId(int id);
    ColoniaDto obtenerColoniaPorId(int id);
    CalleDto obtenerCallePorId(int id);

    ModalidadDto obtenerModalidadPorId(Integer id);
    SubmodalidadDto obtenerSubmodalidadPorId(Integer id);

    ArmaMarcaDto obtenerArmaMarcaPorId(Integer id);
    ArmaClaseDto obtenerArmaClasePorId(Integer id);

    TipoInfraestructuraDto obtenerTipoInfraestructuraPorId(Integer id);

    PersonalNacionalidadDto obtenerNacionalidadPorId(Integer id);
    PersonalPuestoDeTrabajoDto obtenerPuestoPorId(Integer id);
    PersonalSubpuestoDeTrabajoDto obtenerSubpuestoPorId(Integer id);

    VehiculoMarcaDto obtenerMarcaPorId(Integer id);
    VehiculoSubmarcaDto obtenerSubmarcaPorId(Integer id);
    VehiculoTipoDto obtenerTipoVehiculoPorId(Integer id);

}
