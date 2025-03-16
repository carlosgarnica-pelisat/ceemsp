package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaComentarioDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Incidencia;
import com.pelisat.cesp.ceemsp.database.model.IncidenciaComentario;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaComentarioRepository;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenciaComentarioServiceImpl implements IncidenciaComentarioService {

    private final Logger logger = LoggerFactory.getLogger(IncidenciaComentarioService.class);
    private final IncidenciaComentarioRepository incidenciaComentarioRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;

    @Autowired
    public IncidenciaComentarioServiceImpl(IncidenciaComentarioRepository incidenciaComentarioRepository, DaoHelper<CommonModel> daoHelper,
                                           UsuarioService usuarioService, IncidenciaRepository incidenciaRepository, DaoToDtoConverter daoToDtoConverter) {
        this.incidenciaComentarioRepository = incidenciaComentarioRepository;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.incidenciaRepository = incidenciaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
    }

    @Override
    public List<IncidenciaComentarioDto> obtenerComentariosPorIncidenciaUuid(String uuid, String incidenciaUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(incidenciaUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los comentarios para la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<IncidenciaComentario> incidenciaComentarios = incidenciaComentarioRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());

        return incidenciaComentarios.stream().map(c -> {
            IncidenciaComentarioDto incidenciaComentarioDto = new IncidenciaComentarioDto();
            incidenciaComentarioDto.setId(c.getId());
            incidenciaComentarioDto.setUuid(c.getUuid());
            incidenciaComentarioDto.setFecha(c.getFechaCreacion().toString());
            incidenciaComentarioDto.setUsuario(usuarioService.getUserById(c.getCreadoPor()));
            incidenciaComentarioDto.setComentario(c.getComentario());
            return incidenciaComentarioDto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IncidenciaComentarioDto modificarComentario(String uuid, String incidenciaUuid, String comentarioUuid, String username, IncidenciaComentarioDto comentarioDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(comentarioUuid) || StringUtils.isBlank(username) || comentarioDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando el comentario por [{}]", comentarioUuid);

        IncidenciaComentario incidenciaComentario = incidenciaComentarioRepository.getByUuidAndEliminadoFalse(comentarioUuid);
        if(incidenciaComentario == null) {
            logger.warn("El comentario no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        incidenciaComentario.setComentario(comentarioDto.getComentario());
        daoHelper.fulfillAuditorFields(false, incidenciaComentario, usuarioDto.getId());
        incidenciaComentarioRepository.save(incidenciaComentario);

        return null;
    }

    @Override
    @Transactional
    public IncidenciaComentarioDto eliminarComentario(String uuid, String incidenciaUuid, String comentarioUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(comentarioUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando el comentario por [{}]", comentarioUuid);

        IncidenciaComentario incidenciaComentario = incidenciaComentarioRepository.getByUuidAndEliminadoFalse(comentarioUuid);
        if(incidenciaComentario == null) {
            logger.warn("El comentario no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        incidenciaComentario.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, incidenciaComentario, usuarioDto.getId());
        incidenciaComentarioRepository.save(incidenciaComentario);

        return null;
    }
}
