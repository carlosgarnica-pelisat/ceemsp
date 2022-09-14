package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.SubmodalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.ModalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.SubmodalidadRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmodalidadServiceImpl implements SubmodalidadService{
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final SubmodalidadRepository submodalidadRepository;
    private final ModalidadRepository modalidadRepository;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;

    private final Logger logger = LoggerFactory.getLogger(SubmodalidadService.class);

    @Autowired
    public SubmodalidadServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, SubmodalidadRepository submodalidadRepository,
                                   UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper, ModalidadRepository modalidadRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.submodalidadRepository = submodalidadRepository;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.modalidadRepository = modalidadRepository;
    }


    @Override
    public List<SubmodalidadDto> obtenerSubmodalidadesPorModalidad(int modalidadId) {
        if(modalidadId < 1) {
            logger.warn("El id esta viniendo como nulo o vacio.");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo todas las submodalidades con la modalidad id [{}]", modalidadId);

        List<Submodalidad> submodalidades = submodalidadRepository.getAllByCategoriaAndEliminadoFalse(modalidadId);

        return submodalidades.stream()
                .map(daoToDtoConverter::convertDaoToDtoSubmodalidad)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubmodalidadDto> obtenerSubmodalidadesPorModalidadUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las submodalidades con el uuid [{}]", uuid);
        Modalidad modalidad = modalidadRepository.findByUuidAndEliminadoFalse(uuid);
        if(modalidad == null) {
            logger.warn("No existe la modalidad");
            throw new NotFoundResourceException();
        }

        return obtenerSubmodalidadesPorModalidad(modalidad.getId());
    }

    @Override
    public SubmodalidadDto obtenerSubmodalidadPorId(int modalidadId) {
        if(modalidadId < 1) {
            logger.warn("El id esta viniendo como nulo o vacio.");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la submodalidad con el id [{}]", modalidadId);
        Submodalidad submodalidad = submodalidadRepository.getOne(modalidadId);

        return daoToDtoConverter.convertDaoToDtoSubmodalidad(submodalidad);
    }

    @Override
    public SubmodalidadDto guardarSubmodalidad(String modalidadUuid, String username, SubmodalidadDto submodalidadDto) {
        if(StringUtils.isBlank(modalidadUuid) || StringUtils.isBlank(username) || submodalidadDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Guardando nueva submodalidad con la modaliudad [{}]", modalidadUuid);

        Modalidad modalidad = modalidadRepository.findByUuidAndEliminadoFalse(modalidadUuid);

        if(modalidad == null) {
            logger.info("La modalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Submodalidad submodalidad = dtoToDaoConverter.convertDtoToDaoSubmodalidad(submodalidadDto);
        submodalidad.setCategoria(modalidad.getId());
        daoHelper.fulfillAuditorFields(true, submodalidad, usuarioDto.getId());
        submodalidadRepository.save(submodalidad);

        return daoToDtoConverter.convertDaoToDtoSubmodalidad(submodalidad);
    }

    @Override
    public SubmodalidadDto modificarSubmodalidad(String modalidadUuid, String submodalidadUuid, String username, SubmodalidadDto submodalidadDto) {
        if(StringUtils.isBlank(modalidadUuid) || StringUtils.isBlank(username) || submodalidadDto == null || StringUtils.isBlank(submodalidadUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la submodalidad con el uuid [{}]", submodalidadUuid);

        Submodalidad submodalidad = submodalidadRepository.getByUuidAndEliminadoFalse(submodalidadUuid);

        if(submodalidad == null) {
            logger.warn("La submodalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        submodalidad.setNombre(submodalidadDto.getNombre());
        submodalidad.setDescripcion(submodalidadDto.getDescripcion());
        submodalidad.setArmas(submodalidadDto.getArmas());
        submodalidad.setCanes(submodalidadDto.getCanes());
        daoHelper.fulfillAuditorFields(false, submodalidad, usuarioDto.getId());
        submodalidadRepository.save(submodalidad);

        return daoToDtoConverter.convertDaoToDtoSubmodalidad(submodalidad);
    }

    @Override
    public SubmodalidadDto eliminarSubmodalidad(String modalidadUuid, String submodalidadUuid, String username) {
        if(StringUtils.isBlank(modalidadUuid) || StringUtils.isBlank(submodalidadUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Modificando la submarca con el uuid [{}]", submodalidadUuid);

        Submodalidad submodalidad = submodalidadRepository.getByUuidAndEliminadoFalse(submodalidadUuid);

        if(submodalidad == null) {
            logger.warn("La submodalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        submodalidad.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, submodalidad, usuarioDto.getId());
        submodalidadRepository.save(submodalidad);

        return daoToDtoConverter.convertDaoToDtoSubmodalidad(submodalidad);
    }
}
