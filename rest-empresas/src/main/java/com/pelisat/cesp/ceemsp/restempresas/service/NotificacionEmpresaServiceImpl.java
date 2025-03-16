package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionEmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.NotificacionArgos;
import com.pelisat.cesp.ceemsp.database.model.NotificacionEmpresa;
import com.pelisat.cesp.ceemsp.database.repository.NotificacionEmpresaRepository;
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
public class NotificacionEmpresaServiceImpl implements NotificacionEmpresaService {

    private final UsuarioService usuarioService;
    private final NotificacionEmpresaRepository notificacionEmpresaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(NotificacionEmpresaService.class);
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public NotificacionEmpresaServiceImpl(UsuarioService usuarioService, NotificacionEmpresaRepository notificacionEmpresaRepository, DaoToDtoConverter daoToDtoConverter, DaoHelper<CommonModel> daoHelper) {
        this.usuarioService = usuarioService;
        this.notificacionEmpresaRepository = notificacionEmpresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<NotificacionEmpresaDto> obtenerNotificacionesPorUsuario(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        List<NotificacionEmpresa> notificacionesPersonales = notificacionEmpresaRepository.getAllByEmpresaAndEliminadoFalse(usuario.getEmpresa().getId());

        return notificacionesPersonales.stream()
                .sorted(Comparator.comparing(NotificacionEmpresa::getFechaCreacion))
                .map(daoToDtoConverter::convertDaoToDtoNotificacionEmpresa)
                .collect(Collectors.toList());
    }

    @Override
    public NotificacionEmpresaDto leerNotificacion(String uuid, String username) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        NotificacionEmpresa notificacion = notificacionEmpresaRepository.getByUuidAndEliminadoFalse(uuid);
        if(!notificacion.isLeido()) {
            notificacion.setLeido(true);
            daoHelper.fulfillAuditorFields(false, notificacion, usuario.getId());
            notificacionEmpresaRepository.save(notificacion);
        }

        return daoToDtoConverter.convertDaoToDtoNotificacionEmpresa(notificacion);
    }
}
