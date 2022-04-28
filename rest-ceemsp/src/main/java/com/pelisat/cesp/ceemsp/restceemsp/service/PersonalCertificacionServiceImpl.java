package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonalCertificacionRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalCertificacionServiceImpl implements PersonalCertificacionService {

    private final Logger logger = LoggerFactory.getLogger(PersonalCertificacionService.class);
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final PersonalCertificacionRepository personalCertificacionRepository;
    private final PersonaRepository personaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;

    @Autowired
    public PersonalCertificacionServiceImpl(EmpresaService empresaService, UsuarioService usuarioService,
                                            PersonalCertificacionRepository personalCertificacionRepository, DaoToDtoConverter daoToDtoConverter,
                                            DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                            PersonaRepository personaRepository, ArchivosService archivosService) {
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.personalCertificacionRepository = personalCertificacionRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.personaRepository = personaRepository;
        this.archivosService = archivosService;
    }

    @Override
    public List<PersonalCertificacionDto> obtenerCertificacionesPorPersona(String empresaUuid, String personaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid)) {
            logger.warn("El uuid de la persona o de la empresa vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las certificaciones para la persona [{}]", personaUuid);
        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        if(personal == null) {
            logger.warn("La persona o el personal en la base de datos no existen");
            throw new NotFoundResourceException();
        }

        List<PersonalCertificacion> personalCertificaciones = personalCertificacionRepository.getAllByPersonalAndEliminadoFalse(personal.getId());

        return personalCertificaciones.stream().map(daoToDtoConverter::convertDaoToDtoPersonalCertificacion).collect(Collectors.toList());
    }

    @Override
    public File obtenerPdfCertificacion(String empresaUuid, String personaUuid, String certificaionUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid) || StringUtils.isBlank(certificaionUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando la certificacion en PDF con el uuid [{}]", certificaionUuid);

        PersonalCertificacion personalCertificacion = personalCertificacionRepository.findByUuidAndEliminadoFalse(certificaionUuid);

        if(personalCertificacion == null) {
            logger.warn("El certificado no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        if(StringUtils.isBlank(personalCertificacion.getRutaArchivo())) {
            logger.warn("No hay archivo definido para la certificacion");
            throw new NotFoundResourceException();
        }

        File personalCertificacionPdf = new File(personalCertificacion.getRutaArchivo());

        if(!personalCertificacionPdf.exists() &&
                personalCertificacionPdf.isDirectory()) {
            logger.warn("El archvo no existe en el sistema de archivos");
            throw new NotFoundResourceException();
        }

        return personalCertificacionPdf;
    }

    @Override
    @Transactional
    public PersonalCertificacionDto guardarCertificacion(String empresaUuid, String personaUuid, String username, PersonalCertificacionDto personalCertificacionDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid) || StringUtils.isBlank(username) || personalCertificacionDto == null || multipartFile == null) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("La certificacion se esta guardando");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        PersonalCertificacion personalCertificacion = dtoToDaoConverter.convertDtoToDaoPersonalCertificacion(personalCertificacionDto);
        daoHelper.fulfillAuditorFields(true, personalCertificacion, usuarioDto.getId());
        personalCertificacion.setFechaFin(LocalDate.parse(personalCertificacionDto.getFechaFin()));
        personalCertificacion.setFechaInicio(LocalDate.parse(personalCertificacionDto.getFechaInicio()));
        personalCertificacion.setPersonal(personal.getId());

        String ruta = "";

        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.CERTIFICACION_PERSONAL, empresaUuid);
            personalCertificacion.setRutaArchivo(ruta);
            PersonalCertificacion certificacionCreada = personalCertificacionRepository.save(personalCertificacion);

            return daoToDtoConverter.convertDaoToDtoPersonalCertificacion(certificacionCreada);
        } catch(Exception ex) {
            logger.warn(ex.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    @Transactional
    public PersonalCertificacionDto modificarCertificacion(String empresaUuid, String personaUuid, String certificacionUuid, String username, PersonalCertificacionDto personalCertificacionDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid) || StringUtils.isBlank(certificacionUuid) || StringUtils.isBlank(username) || personalCertificacionDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta modificando la certificacion de la persona con el uuid [{}]", certificacionUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        PersonalCertificacion personalCertificacion = personalCertificacionRepository.findByUuidAndEliminadoFalse(certificacionUuid);

        if(personalCertificacion == null) {
            logger.warn("El socio a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Eliminando y modificando");
            archivosService.eliminarArchivo(personalCertificacion.getRutaArchivo());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.CERTIFICACION_PERSONAL, empresaUuid);
                personalCertificacion.setRutaArchivo(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        personalCertificacion.setFechaInicio(LocalDate.parse(personalCertificacionDto.getFechaInicio()));
        personalCertificacion.setFechaFin(LocalDate.parse(personalCertificacionDto.getFechaFin()));
        personalCertificacion.setDuracion(personalCertificacionDto.getDuracion());
        personalCertificacion.setNombre(personalCertificacionDto.getNombre());
        personalCertificacion.setNombreInstructor(personalCertificacionDto.getNombreInstructor());
        daoHelper.fulfillAuditorFields(false, personalCertificacion, usuario.getId());

        personalCertificacionRepository.save(personalCertificacion);

        return daoToDtoConverter.convertDaoToDtoPersonalCertificacion(personalCertificacion);
    }

    @Override
    public PersonalCertificacionDto eliminarCertificacion(String empresaUuid, String personaUuid, String certificacionUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid) || StringUtils.isBlank(certificacionUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta eliminando la certificacion de la persona con el uuid [{}]", certificacionUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        PersonalCertificacion personalCertificacion = personalCertificacionRepository.findByUuidAndEliminadoFalse(certificacionUuid);

        if(personalCertificacion == null) {
            logger.warn("El socio a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personalCertificacion.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, personalCertificacion, usuario.getId());
        personalCertificacionRepository.save(personalCertificacion);
        return daoToDtoConverter.convertDaoToDtoPersonalCertificacion(personalCertificacion);
    }
}
