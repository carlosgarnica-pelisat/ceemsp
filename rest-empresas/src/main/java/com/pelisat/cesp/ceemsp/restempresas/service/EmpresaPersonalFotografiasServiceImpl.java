package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.PersonalFotografia;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonalFotografiaRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaPersonalFotografiasServiceImpl implements EmpresaPersonalFotografiasService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaPersonalFotografiasService.class);
    private final PersonalFotografiaRepository personalFotografiaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final ArchivosService archivosService;
    private final PersonaRepository personaRepository;

    @Autowired
    public EmpresaPersonalFotografiasServiceImpl(PersonalFotografiaRepository personalFotografiaRepository, DaoToDtoConverter daoToDtoConverter,
                                         DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                         UsuarioService usuarioService, ArchivosService archivosService, PersonaRepository personaRepository) {
        this.personalFotografiaRepository = personalFotografiaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.archivosService = archivosService;
        this.personaRepository = personaRepository;
    }

    @Override
    public List<PersonalFotografiaMetadata> mostrarPersonalFotografias(String personalUuid) {
        if(StringUtils.isBlank(personalUuid)) {
            logger.warn("El uuid de la empresa o la persona vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Mostrando los metadatos de las fotografias del personal con id [{}]", personalUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personalUuid);
        if(personal == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<PersonalFotografia> metadata = personalFotografiaRepository.getAllByPersonalAndEliminadoFalse(personal.getId());

        return metadata.stream().map(m -> {
            PersonalFotografiaMetadata pfm = new PersonalFotografiaMetadata();
            String[] tokens = m.getUbicacionArchivo().split("[\\\\|/]");
            pfm.setId(m.getId());
            pfm.setDescripcion(m.getDescripcion());
            pfm.setUuid(m.getUuid());
            pfm.setNombreArchivo(tokens[tokens.length - 1]);
            return pfm;
        }).collect(Collectors.toList());
    }

    @Override
    public File descargarFotografiaPersona(String personalUuid, String fotografiaUuid) {
        if(StringUtils.isBlank(personalUuid) || StringUtils.isBlank(fotografiaUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando la fotografia con uuid [{}]", fotografiaUuid);
        PersonalFotografia personalFotografia = personalFotografiaRepository.getByUuidAndEliminadoFalse(fotografiaUuid);

        if(personalFotografia == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(personalFotografia.getUbicacionArchivo());
    }

    @Override
    @Transactional
    public void guardarPersonalFotografia(String personalUuid, String username, MultipartFile multipartFile, PersonalFotografiaMetadata metadata) {
        if (StringUtils.isBlank(personalUuid) || StringUtils.isBlank(username) || multipartFile == null) {
            logger.warn("El uuid de la empresa o la persona o la foto vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando el archivo en la base de datos");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personalUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonalFotografia personalFotografia = new PersonalFotografia();
        personalFotografia.setPersonal(persona.getId());
        personalFotografia.setDescripcion(metadata.getDescripcion());
        daoHelper.fulfillAuditorFields(true, personalFotografia, usuarioDto.getId());

        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.FOTOGRAFIA_PERSONA, usuarioDto.getEmpresa().getUuid());
            personalFotografia.setUbicacionArchivo(ruta);
            personalFotografiaRepository.save(personalFotografia);

            if(!persona.isFotografiaCapturada()) {
                persona.setFotografiaCapturada(true);
                daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
                personaRepository.save(persona);
            }
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    @Transactional
    public PersonalFotografiaMetadata eliminarPersonalFotografia(String personalUuid, String fotografiaUuid, String username) {
        if(StringUtils.isBlank(personalUuid) || StringUtils.isBlank(fotografiaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la fotografia con uuid [{}]", fotografiaUuid);

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personalUuid);
        if(persona == null) {
            logger.warn("La persona no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        List<PersonalFotografia> personalFotografias = personalFotografiaRepository.getAllByPersonalAndEliminadoFalse(persona.getId());

        PersonalFotografia personalFotografia = personalFotografiaRepository.getByUuidAndEliminadoFalse(fotografiaUuid);

        if(personalFotografia == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        archivosService.eliminarArchivo(personalFotografia.getUbicacionArchivo());
        personalFotografia.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, personalFotografia, usuarioDto.getId());
        personalFotografiaRepository.save(personalFotografia);

        if(personalFotografias.size() <= 1) {
            persona.setFotografiaCapturada(false);
            daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
            personaRepository.save(persona);
        }

        PersonalFotografiaMetadata personalFotografiaMetadata = new PersonalFotografiaMetadata();
        String[] tokens = personalFotografia.getUbicacionArchivo().split("[\\\\|/]");
        personalFotografiaMetadata.setId(personalFotografia.getId());
        personalFotografiaMetadata.setDescripcion(personalFotografia.getDescripcion());
        personalFotografiaMetadata.setUuid(personalFotografia.getUuid());
        personalFotografiaMetadata.setNombreArchivo(tokens[tokens.length - 1]);

        return personalFotografiaMetadata;
    }
}
