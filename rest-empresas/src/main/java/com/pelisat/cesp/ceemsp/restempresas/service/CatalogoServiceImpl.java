package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    private final Logger logger = LoggerFactory.getLogger(CatalogoService.class);
    private final CanRazaRepository canRazaRepository;
    private final CanTipoAdiestramientoRepository canTipoAdiestramientoRepository;
    private final ArmaClaseRepository armaClaseRepository;
    private final ArmaMarcaRepository armaMarcaRepository;
    private final ArmaTipoRepository armaTipoRepository;
    private final ModalidadRepository modalidadRepository;
    private final PersonalNacionalidadRepository personalNacionalidadRepository;
    private final PersonalPuestoRepository personalPuestoRepository;
    private final PersonalSubpuestoRepository personalSubpuestoRepository;
    private final TipoInfraestructuraRepository tipoInfraestructuraRepository;
    private final UniformeRepository uniformeRepository;
    private final VehiculoUsoRepository vehiculoUsoRepository;
    private final VehiculoMarcaRepository vehiculoMarcaRepository;
    private final VehiculoSubmarcaRepository vehiculoSubmarcaRepository;
    private final DaoToDtoConverter daoToDtoConverter;

    @Autowired
    public CatalogoServiceImpl(CanRazaRepository canRazaRepository, CanTipoAdiestramientoRepository canTipoAdiestramientoRepository,
                               ArmaClaseRepository armaClaseRepository, ArmaMarcaRepository armaMarcaRepository, ArmaTipoRepository armaTipoRepository,
                               ModalidadRepository modalidadRepository, PersonalNacionalidadRepository personalNacionalidadRepository,
                               PersonalPuestoRepository personalPuestoRepository, PersonalSubpuestoRepository personalSubpuestoRepository,
                               TipoInfraestructuraRepository tipoInfraestructuraRepository, VehiculoMarcaRepository vehiculoMarcaRepository,
                               VehiculoSubmarcaRepository vehiculoSubmarcaRepository, DaoToDtoConverter daoToDtoConverter,
                               VehiculoUsoRepository vehiculoUsoRepository, UniformeRepository uniformeRepository) {
        this.canRazaRepository = canRazaRepository;
        this.canTipoAdiestramientoRepository = canTipoAdiestramientoRepository;
        this.armaClaseRepository = armaClaseRepository;
        this.armaMarcaRepository = armaMarcaRepository;
        this.armaTipoRepository = armaTipoRepository;
        this.modalidadRepository = modalidadRepository;
        this.personalNacionalidadRepository = personalNacionalidadRepository;
        this.personalPuestoRepository = personalPuestoRepository;
        this.personalSubpuestoRepository = personalSubpuestoRepository;
        this.tipoInfraestructuraRepository = tipoInfraestructuraRepository;
        this.vehiculoMarcaRepository = vehiculoMarcaRepository;
        this.vehiculoSubmarcaRepository = vehiculoSubmarcaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.vehiculoUsoRepository = vehiculoUsoRepository;
        this.uniformeRepository = uniformeRepository;
    }

    @Override
    public List<CanRazaDto> obtenerCanesRazas() {
        return null;
    }

    @Override
    public List<CanTipoAdiestramientoDto> obtenerCanesAdiestramientos() {
        return null;
    }

    @Override
    public List<ArmaClaseDto> obtenerClasesArmas() {
        return null;
    }

    @Override
    public List<ArmaMarcaDto> obtenerArmasMarcas() {
        return null;
    }

    @Override
    public List<ArmaTipoDto> obtenerArmasTipos() {
        return null;
    }

    @Override
    public List<ModalidadDto> obtenerModalidades() {
        return null;
    }

    @Override
    public List<PersonalNacionalidadDto> obtenerNacionalidades() {
        return null;
    }

    @Override
    public List<PersonalPuestoDeTrabajoDto> obtenerPuestosDeTrabajo() {
        return null;
    }

    @Override
    public List<TipoInfraestructuraDto> obtenerTiposInfraestructura() {
        return null;
    }

    @Override
    public List<UniformeDto> obtenerUniformes() {
        return null;
    }

    @Override
    public List<VehiculoUsoDto> obtenerUsosVehiculos() {
        return null;
    }

    @Override
    public List<VehiculoMarcaDto> obtenerMarcasVehiculos() {
        return null;
    }
}
