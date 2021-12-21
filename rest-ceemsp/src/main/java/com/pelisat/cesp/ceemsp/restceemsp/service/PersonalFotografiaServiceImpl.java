package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.PersonalFotografia;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonalFotografiaRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosServiceImpl;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InvalidObjectException;

@Service
public class PersonalFotografiaServiceImpl implements PersonalFotografiaService {

    private final Logger logger = LoggerFactory.getLogger(PersonalFotografiaService.class);
    private final PersonalFotografiaRepository personalFotografiaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final ArchivosService archivosService;
    private final PersonaRepository personaRepository;

    @Autowired
    public PersonalFotografiaServiceImpl(PersonalFotografiaRepository personalFotografiaRepository, DaoToDtoConverter daoToDtoConverter,
                                         DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, EmpresaService empresaService,
                                         UsuarioService usuarioService, ArchivosService archivosService, PersonaRepository personaRepository) {
        this.personalFotografiaRepository = personalFotografiaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.archivosService = archivosService;
        this.personaRepository = personaRepository;
    }

    @Override
    public void mostrarPersonalFotografias(String uuid, String personalUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(personalUuid)) {
            logger.warn("El uuid de la empresa o la persona vienen como nulos o vacios");
            throw new InvalidDataException();
        }


    }

    @Override
    public void guardarPersonalFotografia(String uuid, String personalUuid, String username, MultipartFile multipartFile) {
        if (StringUtils.isBlank(uuid) || StringUtils.isBlank(personalUuid) || StringUtils.isBlank(username) || multipartFile == null) {
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
        daoHelper.fulfillAuditorFields(true, personalFotografia, usuarioDto.getId());

        try {
            String ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.FOTOGRAFIA_PERSONA);
            personalFotografia.setUbicacionArchivo(ruta);
            personalFotografiaRepository.save(personalFotografia);
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            throw new InvalidDataException();
        }

        //TODO: Agregar encriptacion
    }
}
