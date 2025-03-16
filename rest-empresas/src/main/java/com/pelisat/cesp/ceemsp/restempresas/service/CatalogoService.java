package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.ArmaMarca;
import com.pelisat.cesp.ceemsp.database.model.PersonalNacionalidad;
import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;

import java.util.List;

public interface CatalogoService {
    List<CanRazaDto> obtenerCanesRazas();
    CanRazaDto obtenerCanRazaPorId(int id);

    List<CanTipoAdiestramientoDto> obtenerCanesAdiestramientos();
    CanTipoAdiestramientoDto obtenerCanAdiestramientoPorId(int id);

    List<ArmaClaseDto> obtenerClasesArmas();
    List<ArmaMarcaDto> obtenerArmasMarcas();
    List<ArmaTipoDto> obtenerArmasTipos();

    List<ModalidadDto> obtenerModalidades();

    List<PersonalNacionalidadDto> obtenerNacionalidades();
    List<PersonalPuestoDeTrabajoDto> obtenerPuestosDeTrabajo();

    List<TipoInfraestructuraDto> obtenerTiposInfraestructura();

    List<UniformeDto> obtenerUniformes();
    UniformeDto obtenerUniformePorId(int id);

    List<EquipoDto> obtenerEquipos();
    List<EquipoDto> obtenerEquiposCalificables(String username);
    EquipoDto obtenerEquipoPorId(int id);

    List<VehiculoUsoDto> obtenerUsosVehiculos();
    VehiculoUsoDto obtenerUsoVehiculoPorId(int id);

    List<VehiculoMarcaDto> obtenerMarcasVehiculos();
    List<VehiculoMarcaDto> obtenerMarcasVehiculosTipo(VehiculoTipoEnum vehiculoTipo);
    VehiculoMarcaDto obtenerMarcaVehiculoPorId(int id);
    VehiculoMarcaDto obtenerMarcaVehiculoPorUuid(String uuid);

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

    List<PersonalSubpuestoDeTrabajoDto> obtenerSubpuestosPorUuid(String uuid);

    VehiculoMarcaDto obtenerMarcaPorId(Integer id);
    VehiculoSubmarcaDto obtenerSubmarcaPorId(Integer id);

    List<VehiculoTipoDto> obtenerTiposVehiculo();
    VehiculoTipoDto obtenerTipoVehiculoPorId(Integer id);

}
