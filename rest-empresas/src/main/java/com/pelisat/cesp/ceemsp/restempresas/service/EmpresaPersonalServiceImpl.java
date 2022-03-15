package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaPersonalServiceImpl implements EmpresaPersonalService {
    private final DaoHelper<CommonModel> daoHelper;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final CatalogoService catalogoService;
    private final EmpresaPersonalFotografiasService personalFotografiaService;
    private final EmpresaPersonalCertificacionService personalCertificacionService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaPersonalService.class);

    @Autowired
    public EmpresaPersonalServiceImpl(DaoHelper<CommonModel> daoHelper, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaPersonalFotografiasService empresaPersonalFotografiasService,
                              UsuarioService usuarioService, PersonaRepository personaRepository,
                              CatalogoService catalogoService, EmpresaDomicilioService empresaDomicilioService, EmpresaPersonalCertificacionService empresaPersonalCertificacionService) {
        this.daoHelper = daoHelper;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.personaRepository = personaRepository;
        this.empresaDomicilioService = empresaDomicilioService;
        this.personalCertificacionService = empresaPersonalCertificacionService;
        this.personalFotografiaService = empresaPersonalFotografiasService;
        this.catalogoService = catalogoService;
    }

    @Override
    public List<PersonaDto> obtenerTodos(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Personal> personal = personaRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return personal.stream().map(daoToDtoConverter::convertDaoToDtoPersona).collect(Collectors.toList());
    }

    @Override
    public PersonaDto obtenerPorUuid(String personaUuid) {
        if(StringUtils.isBlank(personaUuid)) {
            logger.warn("El uuid de la empresa o de la persona vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo a la persona con el uuid [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        if(personal == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setCertificaciones(personalCertificacionService.obtenerCertificacionesPorPersona(personaUuid));
        personaDto.setNacionalidad(catalogoService.obtenerNacionalidadPorId(personal.getNacionalidad()));
        personaDto.setPuestoDeTrabajo(catalogoService.obtenerPuestoPorId(personal.getPuesto()));
        personaDto.setSubpuestoDeTrabajo(catalogoService.obtenerSubpuestoPorId(personal.getSubpuesto()));
        personaDto.setDomicilioAsignado(empresaDomicilioService.obtenerPorId(personal.getDomicilioAsignado()));
        personaDto.setFotografias(personalFotografiaService.mostrarPersonalFotografias(personaUuid));
        personaDto.setCalleCatalogo(catalogoService.obtenerCallePorId(personal.getCalleCatalogo()));
        personaDto.setColoniaCatalogo(catalogoService.obtenerColoniaPorId(personal.getColoniaCatalogo()));
        personaDto.setLocalidadCatalogo(catalogoService.obtenerLocalidadPorId(personal.getLocalidadCatalogo()));
        personaDto.setMunicipioCatalogo(catalogoService.obtenerMunicipioPorId(personal.getMunicipioCatalogo()));
        personaDto.setEstadoCatalogo(catalogoService.obtenerEstadoPorId(personal.getEstadoCatalogo()));
        return personaDto;
    }

    @Override
    public PersonaDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("La empresa o el id vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la persona con el id [{}]", id);

        Personal personal = personaRepository.getOne(id);

        if(personal == null || personal.getEliminado())  {
            logger.warn("La persona no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setNacionalidad(catalogoService.obtenerNacionalidadPorId(personal.getNacionalidad()));

        return personaDto;
    }

    @Override
    public PersonaDto crearNuevo(PersonaDto personalDto, String username) {
        if(StringUtils.isBlank(username) || personalDto == null) {
            logger.warn("El usuario, la empresa o el personal a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo personal");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Personal personal = dtoToDaoConverter.convertDtoToDaoPersonal(personalDto);
        personal.setFechaNacimiento(LocalDate.parse(personalDto.getFechaNacimiento()));
        personal.setFechaIngreso(LocalDate.parse(personalDto.getFechaIngreso()));
        daoHelper.fulfillAuditorFields(true, personal, usuarioDto.getId());
        personal.setEmpresa(usuarioDto.getEmpresa().getId());
        personal.setCalleCatalogo(personalDto.getCalleCatalogo().getId());
        personal.setColoniaCatalogo(personalDto.getColoniaCatalogo().getId());
        personal.setLocalidadCatalogo(personalDto.getLocalidadCatalogo().getId());
        personal.setMunicipioCatalogo(personalDto.getMunicipioCatalogo().getId());
        personal.setEstadoCatalogo(personalDto.getEstadoCatalogo().getId());
        personal.setNacionalidad(personalDto.getNacionalidad().getId());
        personal.setDomicilio1(personalDto.getCalleCatalogo().getNombre());
        personal.setDomicilio2(personalDto.getColoniaCatalogo().getNombre());
        personal.setLocalidad(personalDto.getLocalidadCatalogo().getNombre());
        personal.setEstado(personalDto.getEstadoCatalogo().getNombre());
        personal.setDomicilio3(personalDto.getMunicipioCatalogo().getNombre());

        Personal personalCreado = personaRepository.save(personal);

        PersonaDto response = daoToDtoConverter.convertDaoToDtoPersona(personalCreado);
        return response;
    }

    @Override
    public PersonaDto modificarInformacionPuesto(PersonaDto personaDto, String username, String personaUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(personaUuid) || personaDto == null) {
            logger.warn("El usuario, la empresa, la persona o la informacion del trabajo a modificar vienen como nulas o invalidas");
            throw new InvalidDataException();
        }

        logger.info("Modificando los detalles de trabajo para la persona [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(personal == null) {
            logger.warn("La persona a cambiar la informacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        // TODO: Hacer las validaciones respecto al tipo
        personal.setPuesto(personaDto.getPuestoDeTrabajo().getId());
        personal.setSubpuesto(personaDto.getSubpuestoDeTrabajo().getId());
        personal.setDetallesPuesto(personaDto.getDetallesPuesto());
        personal.setDomicilioAsignado(personaDto.getDomicilioAsignado().getId());
        personal.setEstatusCuip(personaDto.getEstatusCuip());
        personal.setCuip(personaDto.getCuip());
        personal.setNumeroVolanteCuip(personaDto.getCuip());

        if(personaDto.getModalidad() != null) {
            logger.info("La informacion del trabajo incluye modalidad");
            personal.setModalidad(personaDto.getModalidad().getId());
        }

        if(!StringUtils.isBlank(personaDto.getFechaVolanteCuip())) {
            logger.info("El volante de CUIP esta puesto. Convirtiendo");
            personal.setFechaVolanteCuip(LocalDate.parse(personaDto.getFechaVolanteCuip()));
        }

        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());
        personaRepository.save(personal);

        return daoToDtoConverter.convertDaoToDtoPersona(personal);
    }

    @Override
    public PersonaDto modificarPersona(String personaUuid, String username, PersonaDto personaDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(personaUuid) || personaDto == null) {
            logger.warn("El usuario, la empresa, la persona o la informacion del trabajo a modificar vienen como nulas o invalidas");
            throw new InvalidDataException();
        }

        logger.info("Modificando la persona con el uuid [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(personal == null) {
            logger.warn("La persona a cambiar la informacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personal.setNombres(personaDto.getNombres());
        personal.setApellidoPaterno(personaDto.getApellidoPaterno());
        personal.setApellidoMaterno(personaDto.getApellidoMaterno());
        personal.setDomicilio4(personaDto.getDomicilio4());
        personal.setCodigoPostal(personaDto.getCodigoPostal());
        personal.setCurp(personaDto.getCurp());
        personal.setFechaNacimiento(LocalDate.parse(personaDto.getFechaNacimiento()));
        personal.setFechaIngreso(LocalDate.parse(personaDto.getFechaIngreso()));
        personal.setEstadoCivil(personaDto.getEstadoCivil());
        personal.setTelefono(personaDto.getTelefono());
        personal.setCorreoElectronico(personaDto.getCorreoElectronico());
        personal.setTipoSangre(personaDto.getTipoSangre());

        personal.setEstadoCatalogo(personaDto.getEstadoCatalogo().getId());
        personal.setMunicipioCatalogo(personaDto.getMunicipioCatalogo().getId());
        personal.setLocalidadCatalogo(personaDto.getLocalidadCatalogo().getId());
        personal.setColoniaCatalogo(personaDto.getColoniaCatalogo().getId());
        personal.setCalleCatalogo(personaDto.getCalleCatalogo().getId());

        personal.setDomicilio1(personaDto.getCalleCatalogo().getNombre());
        personal.setDomicilio2(personaDto.getColoniaCatalogo().getNombre());
        personal.setLocalidad(personaDto.getLocalidadCatalogo().getNombre());
        personal.setDomicilio3(personaDto.getMunicipioCatalogo().getNombre());
        personal.setEstado(personaDto.getEstadoCatalogo().getNombre());

        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());

        personaRepository.save(personal);
        return daoToDtoConverter.convertDaoToDtoPersona(personal);
    }

    @Override
    public PersonaDto eliminarPersona(String personaUuid, String username) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(personaUuid)) {
            logger.warn("El usuario, la empresa, la persona o la informacion del trabajo a modificar vienen como nulas o invalidas");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la persona con el uuid [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(personal == null) {
            logger.warn("La persona a eliminar la informacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personal.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());
        personaRepository.save(personal);
        return daoToDtoConverter.convertDaoToDtoPersona(personal);
    }
}
