package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionArgosDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.NotificacionArgos;
import com.pelisat.cesp.ceemsp.database.repository.NotificacionArgosRepository;
import com.pelisat.cesp.ceemsp.database.type.NotificacionArgosTipoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionArgosServiceImpl implements NotificacionArgosService {

    private final UsuarioService usuarioService;
    private final NotificacionArgosRepository notificacionArgosRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(NotificacionArgosService.class);
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public NotificacionArgosServiceImpl(UsuarioService usuarioService, NotificacionArgosRepository notificacionArgosRepository,
                                        DaoToDtoConverter daoToDtoConverter, DaoHelper<CommonModel> daoHelper) {
        this.usuarioService = usuarioService;
        this.notificacionArgosRepository = notificacionArgosRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<NotificacionArgosDto> obtenerNotificacionesPorUsuario(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        List<NotificacionArgos> notificacionesPersonales = notificacionArgosRepository.getAllByUsuarioAndEliminadoFalse(usuario.getId());
        List<NotificacionArgos> notificacionesGenerales = notificacionArgosRepository.getAllByTipoAndEliminadoFalse(NotificacionArgosTipoEnum.GENERAL);

        List<NotificacionArgos> notificaciones = new ArrayList<>();
        notificaciones.addAll(notificacionesPersonales);
        notificaciones.addAll(notificacionesGenerales);

        return notificaciones.stream()
                .sorted(Comparator.comparing(NotificacionArgos::getFechaCreacion).reversed())
                .map(daoToDtoConverter::convertDaoToDtoNotificacionArgos)
                .collect(Collectors.toList())
                ;
    }

    @Override
    public NotificacionArgosDto leerNotificacion(String uuid, String username) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        NotificacionArgos notificacion = notificacionArgosRepository.getByUuidAndEliminadoFalse(uuid);
        if(!notificacion.isLeido()) {
            notificacion.setLeido(true);
            daoHelper.fulfillAuditorFields(false, notificacion, usuario.getId());
            notificacionArgosRepository.save(notificacion);
        }

        return daoToDtoConverter.convertDaoToDtoNotificacionArgos(notificacion);
    }
}
