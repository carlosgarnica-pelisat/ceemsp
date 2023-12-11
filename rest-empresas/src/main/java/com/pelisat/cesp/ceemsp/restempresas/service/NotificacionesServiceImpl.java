package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.BuzonInterno;
import com.pelisat.cesp.ceemsp.database.model.BuzonInternoDestinatario;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoDestinatarioRepository;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionesServiceImpl implements NotificacionesService {
    private final BuzonInternoRepository buzonInternoRepository;
    private final BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(NotificacionesServiceImpl.class);

    @Autowired
    public NotificacionesServiceImpl(BuzonInternoRepository buzonInternoRepository, BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository,
                                     UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter, DaoHelper<CommonModel> daoHelper) {
        this.buzonInternoRepository = buzonInternoRepository;
        this.buzonInternoDestinatarioRepository = buzonInternoDestinatarioRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<BuzonInternoDto> obtenerNotificacionesPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<BuzonInternoDestinatario> notificacionesEmpresa = buzonInternoDestinatarioRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return notificacionesEmpresa.stream().map(n -> {
            BuzonInterno buzonInterno = buzonInternoRepository.getByIdAndEliminadoFalse(n.getBuzonInterno());
            if(buzonInterno == null) {
                return null;
            }
            BuzonInternoDto buzonInternoDto = daoToDtoConverter.convertDaoToDtoBuzonInterno(buzonInterno);
            buzonInternoDto.setLeido(n.isVisto());
            return buzonInternoDto;
        }).filter(x -> x!= null)
        .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BuzonInternoDto leerNotificacion(String username, String uuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        BuzonInterno buzonInterno = buzonInternoRepository.getByUuidAndEliminadoFalse(uuid);

        BuzonInternoDestinatario buzonInternoDestinatario = buzonInternoDestinatarioRepository.getByBuzonInternoAndEmpresaAndEliminadoFalse(buzonInterno.getId(), usuarioDto.getEmpresa().getId());

        if(buzonInternoDestinatario == null) {
            logger.warn("El destinatario para esta notificacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(!buzonInternoDestinatario.isVisto()) {
            buzonInternoDestinatario.setVisto(true);
            buzonInternoDestinatario.setFechaVisto(LocalDateTime.now());
            daoHelper.fulfillAuditorFields(false, buzonInternoDestinatario, usuarioDto.getId());
            buzonInternoDestinatarioRepository.save(buzonInternoDestinatario);
        }

        return daoToDtoConverter.convertDaoToDtoBuzonInterno(buzonInterno);
    }
}
