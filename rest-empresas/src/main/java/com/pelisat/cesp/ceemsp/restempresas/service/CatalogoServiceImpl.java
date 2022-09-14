package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final CalleRepository calleRepository;
    private final ColoniaRepository coloniaRepository;
    private final LocalidadRepository localidadRepository;
    private final MunicipioRepository municipioRepository;
    private final EstadoRepository estadoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final SubmodalidadRepository submodalidadRepository;
    private final VehiculoTipoRepository vehiculoTipoRepository;

    @Autowired
    public CatalogoServiceImpl(CanRazaRepository canRazaRepository, CanTipoAdiestramientoRepository canTipoAdiestramientoRepository,
                               ArmaClaseRepository armaClaseRepository, ArmaMarcaRepository armaMarcaRepository, ArmaTipoRepository armaTipoRepository,
                               ModalidadRepository modalidadRepository, PersonalNacionalidadRepository personalNacionalidadRepository,
                               PersonalPuestoRepository personalPuestoRepository, PersonalSubpuestoRepository personalSubpuestoRepository,
                               TipoInfraestructuraRepository tipoInfraestructuraRepository, VehiculoMarcaRepository vehiculoMarcaRepository,
                               VehiculoSubmarcaRepository vehiculoSubmarcaRepository, DaoToDtoConverter daoToDtoConverter,
                               VehiculoUsoRepository vehiculoUsoRepository, UniformeRepository uniformeRepository, CalleRepository calleRepository,
                               ColoniaRepository coloniaRepository, LocalidadRepository localidadRepository, MunicipioRepository municipioRepository,
                               EstadoRepository estadoRepository, SubmodalidadRepository submodalidadRepository, VehiculoTipoRepository vehiculoTipoRepository) {
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
        this.calleRepository = calleRepository;
        this.coloniaRepository = coloniaRepository;
        this.localidadRepository = localidadRepository;
        this.municipioRepository = municipioRepository;
        this.estadoRepository = estadoRepository;
        this.submodalidadRepository = submodalidadRepository;
        this.vehiculoTipoRepository = vehiculoTipoRepository;
    }

    @Override
    public List<CanRazaDto> obtenerCanesRazas() {
        logger.info("Consultando todas las razas guardadas en la base de datos");
        List<CanRaza> canRazas = canRazaRepository.getAllByEliminadoFalse();
        return canRazas.stream()
                .map(daoToDtoConverter::convertDaoToDtoCanRaza)
                .collect(Collectors.toList());
    }

    @Override
    public CanRazaDto obtenerCanRazaPorId(int id) {
        logger.info("Consultando la raza del can con el id [{}]", id);
        CanRaza canRaza = canRazaRepository.getById(id);
        return daoToDtoConverter.convertDaoToDtoCanRaza(canRaza);
    }

    @Override
    public List<CanTipoAdiestramientoDto> obtenerCanesAdiestramientos() {
        logger.info("Consultando todas las razas guardadas en la base de datos");
        List<CanTipoAdiestramiento> canTiposAdiestramiento = canTipoAdiestramientoRepository.getAllByEliminadoFalse();
        return canTiposAdiestramiento.stream()
                .map(daoToDtoConverter::convertDaoToDtoCanTipoAdiestramiento)
                .collect(Collectors.toList());
    }

    @Override
    public CanTipoAdiestramientoDto obtenerCanAdiestramientoPorId(int id) {
        logger.info("Consultando el uniforme con el id [{}]", id);
        CanTipoAdiestramiento canTipoAdiestramiento = canTipoAdiestramientoRepository.getById(id);
        return daoToDtoConverter.convertDaoToDtoCanTipoAdiestramiento(canTipoAdiestramiento);
    }

    @Override
    public List<ArmaClaseDto> obtenerClasesArmas() {
        logger.info("Consultando todas las clases de armas guardadas en la base de datos");
        List<ArmaClase> armaClases = armaClaseRepository.getAllByEliminadoFalse();
        return armaClases.stream()
                .map(daoToDtoConverter::convertDaoToDtoArmaClase)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArmaMarcaDto> obtenerArmasMarcas() {
        logger.info("Consultando todas las clases de armas guardadas en la base de datos");
        List<ArmaMarca> armaMarcas = armaMarcaRepository.getAllByEliminadoFalse();
        return armaMarcas.stream()
                .map(daoToDtoConverter::convertDaoToDtoArmaMarca)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArmaTipoDto> obtenerArmasTipos() {
        logger.info("Consultando todas las clases de armas guardadas en la base de datos");
        List<ArmaTipo> armaTipos = armaTipoRepository.getAllByEliminadoFalse();
        return armaTipos.stream()
                .map(daoToDtoConverter::convertDaoToDtoArmaTipo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModalidadDto> obtenerModalidades() {
        return null;
    }

    @Override
    public List<PersonalNacionalidadDto> obtenerNacionalidades() {
        logger.info("Consultando todas las nacionalidades guardadas en la base de datos");
        List<PersonalNacionalidad> personalNacionalidades = personalNacionalidadRepository.getAllByEliminadoFalse();
        return personalNacionalidades.stream()
                .map(daoToDtoConverter::convertDaoToDtoPersonalNacionalidad)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonalPuestoDeTrabajoDto> obtenerPuestosDeTrabajo() {
        return null;
    }

    @Override
    public List<TipoInfraestructuraDto> obtenerTiposInfraestructura() {
        logger.info("Consultando todos los tipos de infraestructura en la base de datos");
        List<TipoInfraestructura> tipoInfraestructuras = tipoInfraestructuraRepository.getAllByEliminadoFalse();
        return tipoInfraestructuras.stream()
                .map(daoToDtoConverter::convertDaoToDtoTipoInfraestructura)
                .collect(Collectors.toList());
    }

    @Override
    public List<UniformeDto> obtenerUniformes() {
        logger.info("Consultando todos los uniformes guardados en la base de datos");
        List<Uniforme> uniformes = uniformeRepository.findAllByEliminadoFalse();
        return uniformes.stream()
                .map(daoToDtoConverter::convertDaoToDtoUniforme)
                .collect(Collectors.toList());
    }

    @Override
    public UniformeDto obtenerUniformePorId(int id) {
        logger.info("Consultando el uniforme con el id [{}]", id);
        Uniforme uniforme = uniformeRepository.getById(id);
        return daoToDtoConverter.convertDaoToDtoUniforme(uniforme);
    }

    @Override
    public List<VehiculoUsoDto> obtenerUsosVehiculos() {
        logger.info("Consultando todos los usos de vehiculos guardadas en la base de datos");
        List<VehiculoUso> vehiculoUsos = vehiculoUsoRepository.getAllByEliminadoFalse();
        return vehiculoUsos.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculoUso)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoUsoDto obtenerUsoVehiculoPorId(int id) {
        logger.info("Consultando el uniforme con el id [{}]", id);
        VehiculoUso vehiculoUso = vehiculoUsoRepository.getById(id);
        return daoToDtoConverter.convertDaoToDtoVehiculoUso(vehiculoUso);
    }

    @Override
    public List<VehiculoMarcaDto> obtenerMarcasVehiculos() {
        logger.info("Consultando todas las marcas de vehiculos en la base de datos");
        List<VehiculoMarca> armaTipos = vehiculoMarcaRepository.getAllByEliminadoFalse();
        return armaTipos.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculoMarca)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoMarcaDto obtenerMarcaVehiculoPorId(int id) {
        logger.info("Consultando el uniforme con el id [{}]", id);
        VehiculoMarca vehiculoMarca = vehiculoMarcaRepository.getById(id);
        return daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarca);
    }

    @Override
    public List<CalleDto> obtenerCalles(Integer limit) {
        logger.info("Obteniendo todas las calles guardadas en la base de datos.");
        List<Calle> calles = new ArrayList<>();

        if(limit == null) {
            calles = calleRepository.findAllByEliminadoFalse();
        } else {
            calles = calleRepository.findAllByEliminadoFalse(PageRequest.of(0, limit));
        }

        return calles.stream().map(daoToDtoConverter::convertDaoToDtoCalle).collect(Collectors.toList());
    }

    @Override
    public List<CalleDto> obtenerCallesPorQuery(String query) {
        if(StringUtils.isBlank(query)) {
            logger.warn("La query ingresada para la busqueda de calles viene como nula o vacia");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las calles con el query [{}]", query);
        List<Calle> calles = calleRepository.findAllByNombreContainsAndEliminadoFalse(query);

        return calles.stream().map(daoToDtoConverter::convertDaoToDtoCalle).collect(Collectors.toList());
    }

    @Override
    public List<EstadoDto> obtenerTodosLosEstados() {
        List<Estado> estados = estadoRepository.findAll();
        return estados.stream().map(daoToDtoConverter::convertDaoToDtoEstado).collect(Collectors.toList());
    }

    @Override
    public List<MunicipioDto> obtenerMunicipiosPorEstadoUuid(String uuidEstado) {
        if(StringUtils.isBlank(uuidEstado)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los municipios con el estado [{}]", uuidEstado);

        Estado estado = estadoRepository.getByUuidAndEliminadoFalse(uuidEstado);

        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Municipio> municipios = municipioRepository.getAllByEstadoAndEliminadoFalse(estado.getId());
        return municipios.stream().map(daoToDtoConverter::convertDaoToDtoMunicipio).collect(Collectors.toList());
    }

    @Override
    public List<LocalidadDto> obtenerLocalidadesPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid) {
        if(StringUtils.isBlank(uuidEstado) || StringUtils.isBlank(municipioUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las localidades con el estado [{}] y municipio [{}]", uuidEstado, municipioUuid);

        Estado estado = estadoRepository.getByUuidAndEliminadoFalse(uuidEstado);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Municipio municipio = municipioRepository.getByUuid(municipioUuid);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Localidad> localidades = localidadRepository.getAllByEstadoAndMunicipioAndEliminadoFalse(estado.getId(), municipio.getClave());
        return localidades.stream().map(daoToDtoConverter::convertDaoToDtoLocalidad).collect(Collectors.toList());
    }

    @Override
    public List<ColoniaDto> obtenerColoniasPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid) {
        if(StringUtils.isBlank(uuidEstado) || StringUtils.isBlank(municipioUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las colonias con el estado [{}] y municipio [{}]", uuidEstado, municipioUuid);

        Estado estado = estadoRepository.getByUuidAndEliminadoFalse(uuidEstado);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Municipio municipio = municipioRepository.getByUuid(municipioUuid);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Colonia> colonias = coloniaRepository.getAllByEstadoAndMunicipioAndEliminadoFalse(estado.getId(), municipio.getClave());
        return colonias.stream().map(daoToDtoConverter::convertDaoToDtoColonia).collect(Collectors.toList());
    }

    @Override
    public EstadoDto obtenerEstadoPorId(int id) {
        if(id < 1) {
            logger.warn("El id del estado a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el estado por id [{}]", id);
        Estado estado = estadoRepository.getOne(id);

        if(estado == null || estado.getEliminado()) {
            logger.warn("El estado no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoEstado(estado);
    }

    @Override
    public MunicipioDto obtenerMunicipioPorId(int id) {
        if(id < 1) {
            logger.warn("El id del municipio a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el municipio por id [{}]", id);
        Municipio municipio = municipioRepository.getOne(id);

        if(municipio == null || municipio.getEliminado()) {
            logger.warn("El municipio no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoMunicipio(municipio);
    }

    @Override
    public LocalidadDto obtenerLocalidadPorId(int id) {
        if(id < 1) {
            logger.warn("El id de la localidad a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la localidad por id [{}]", id);
        Localidad localidad = localidadRepository.getOne(id);

        if(localidad == null || localidad.getEliminado()) {
            logger.warn("La localidad no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoLocalidad(localidad);
    }

    @Override
    public ColoniaDto obtenerColoniaPorId(int id) {
        if(id < 1) {
            logger.warn("El id de la colonia a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la colonia por id [{}]", id);
        Colonia colonia = coloniaRepository.getOne(id);

        if(colonia == null || colonia.getEliminado()) {
            logger.warn("La colonia no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoColonia(colonia);
    }

    @Override
    public CalleDto obtenerCallePorId(int id) {
        if(id < 1) {
            logger.warn("El id ingresado es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la calle con el id [{}]", id);
        Calle calle = calleRepository.getOne(id);

        if(calle == null || calle.getEliminado()) {
            logger.warn("La calle no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCalle(calle);
    }

    @Override
    public ModalidadDto obtenerModalidadPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id a consultar esta viniendo como invalido o nulo");
            throw new InvalidDataException();
        }

        logger.info("Consultando la modalidad con el id [{}]", id);

        Modalidad modalidad = modalidadRepository.getOne(id);

        if(modalidad == null) {
            logger.warn("El tipo de adiestramiento no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoModalidad(modalidad);
    }

    @Override
    public SubmodalidadDto obtenerSubmodalidadPorId(Integer id) {
        if(id < 1) {
            logger.warn("El id esta viniendo como nulo o vacio.");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la submodalidad con el id [{}]", id);
        Submodalidad submodalidad = submodalidadRepository.getOne(id);

        return daoToDtoConverter.convertDaoToDtoSubmodalidad(submodalidad);
    }

    @Override
    public ArmaMarcaDto obtenerArmaMarcaPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la marca del arma con el id [{}]", id);

        ArmaMarca armaMarca = armaMarcaRepository.getOne(id);
        return daoToDtoConverter.convertDaoToDtoArmaMarca(armaMarca);
    }

    @Override
    public ArmaClaseDto obtenerArmaClasePorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando la clase del arma con el id [{}]", id);

        ArmaClase armaClase = armaClaseRepository.getOne(id);

        if(armaClase == null) {
            logger.warn("El armna clase no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoArmaClase(armaClase);
    }

    @Override
    public TipoInfraestructuraDto obtenerTipoInfraestructuraPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id del tipo de infraestructura a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        TipoInfraestructura tipoInfraestructura = tipoInfraestructuraRepository.getOne(id);

        if(tipoInfraestructura == null) {
            logger.warn("El tipo de infraestructura con id [{}] viene como nula o vacia", id);
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoTipoInfraestructura(tipoInfraestructura);
    }

    @Override
    public PersonalNacionalidadDto obtenerNacionalidadPorId(Integer id) {
        return null;
    }

    @Override
    public PersonalPuestoDeTrabajoDto obtenerPuestoPorId(Integer id) {
        return null;
    }

    @Override
    public PersonalSubpuestoDeTrabajoDto obtenerSubpuestoPorId(Integer id) {
        return null;
    }

    @Override
    public VehiculoMarcaDto obtenerMarcaPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id viene como nulo o vacio");
            throw new InvalidDataException();
        }
        logger.info("Descargando la marca del vehiculo con el id [{}]", id);
        VehiculoMarca vehiculoMarca = vehiculoMarcaRepository.getOne(id);

        if(vehiculoMarca == null) {
            logger.warn("La marca del vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarca);
    }

    @Override
    public VehiculoSubmarcaDto obtenerSubmarcaPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id de la submarca de vehiculo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la submarca con el id [{}]", id);
        VehiculoSubmarca vehiculoSubmarca = vehiculoSubmarcaRepository.getOne(id);

        if(vehiculoSubmarca == null) {
            logger.warn("La submarca del vehiculo viene como nula o vascia");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoSubmarca(vehiculoSubmarca);
    }

    @Override
    public List<VehiculoTipoDto> obtenerTiposVehiculo() {
        logger.info("Consultando todos los tipos de vehiculo en la base de datos");
        List<VehiculoTipo> tiposVehiculo = vehiculoTipoRepository.getAllByEliminadoFalse();
        return tiposVehiculo.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculoTipo)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoTipoDto obtenerTipoVehiculoPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando el tipo de vehiculo con el id [{}]", id);

        VehiculoTipo vehiculoTipo = vehiculoTipoRepository.getOne(id);

        if(vehiculoTipo == null) {
            logger.warn("El armna clase no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipo);
    }
}
