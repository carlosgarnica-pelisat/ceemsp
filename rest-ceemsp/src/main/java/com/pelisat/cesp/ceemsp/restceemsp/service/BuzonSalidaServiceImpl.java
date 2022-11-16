package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDestinatarioDto;
import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.BuzonInterno;
import com.pelisat.cesp.ceemsp.database.model.BuzonInternoDestinatario;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoDestinatarioRepository;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoRepository;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoDestinatarioEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.NotificacionEmailService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuzonSalidaServiceImpl implements BuzonSalidaService {

    private final Logger logger = LoggerFactory.getLogger(BuzonSalidaService.class);
    private final BuzonInternoRepository buzonInternoRepository;
    private final BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository;
    private final NotificacionEmailService notificacionEmailService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;

    @Autowired
    public BuzonSalidaServiceImpl(BuzonInternoRepository buzonInternoRepository, NotificacionEmailService notificacionEmailService,
                                  DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                  UsuarioService usuarioService, BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository,
                                  EmpresaService empresaService) {
        this.buzonInternoRepository = buzonInternoRepository;
        this.notificacionEmailService = notificacionEmailService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.buzonInternoDestinatarioRepository = buzonInternoDestinatarioRepository;
    }

    @Override
    public List<BuzonInternoDto> obtenerTodosLosMensajes() {
        logger.info("Obteniendo todos los mensajes del buzon interno");
        List<BuzonInterno> mensajes = buzonInternoRepository.getAllByEliminadoFalse();
        return mensajes.stream().map(daoToDtoConverter::convertDaoToDtoBuzonInterno).collect(Collectors.toList());
    }

    @Override
    public BuzonInternoDto obtenerBuzonInternoPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el mensaje en el buzon interno con el uuid [{}]", uuid);

        BuzonInterno buzonInterno = buzonInternoRepository.getByUuidAndEliminadoFalse(uuid);

        if(buzonInterno == null) {
            logger.warn("El mensaje en el buzon interno no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<BuzonInternoDestinatario> destinatarios = buzonInternoDestinatarioRepository.getAllByBuzonInternoAndEliminadoFalse(buzonInterno.getId());

        BuzonInternoDto buzonInternoDto = daoToDtoConverter.convertDaoToDtoBuzonInterno(buzonInterno);
        buzonInternoDto.setDestinatarios(destinatarios.stream().map(d -> {
            BuzonInternoDestinatarioDto destinatario = daoToDtoConverter.convertDaoToDtoBuzonInternoDestinatario(d);
            if(d.getTipoDestinatario() == TipoDestinatarioEnum.EMPRESA) {
                destinatario.setEmpresa(empresaService.obtenerPorId(d.getEmpresa()));
            } else if(d.getTipoDestinatario() == TipoDestinatarioEnum.USUARIO) {
                destinatario.setUsuario(usuarioService.getUserById(d.getUsuario()));
            }

            return destinatario;
        }).collect(Collectors.toList()));

        return buzonInternoDto;
    }

    @Override
    public BuzonInternoDto guardarBuzonInterno(BuzonInternoDto buzonInternoDto, String username) {
        if(StringUtils.isBlank(username) || buzonInternoDto == null) {
            logger.warn("Alguno de los parametros es invalido o nulo");
            throw new InvalidDataException();
        }

        logger.info("Guardando y enviando el mensaje en el buzon interno");

        BuzonInterno buzonInterno = dtoToDaoConverter.convertDtoToDaoBuzonInterno(buzonInternoDto);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        daoHelper.fulfillAuditorFields(true, buzonInterno, usuarioDto.getId());

        BuzonInterno buzonInternoCreado = buzonInternoRepository.save(buzonInterno);

        buzonInternoDto.getDestinatarios().forEach(d -> {
            BuzonInternoDestinatario bid = dtoToDaoConverter.convertDtoToDaoBuzonInternoDestinatario(d);
            bid.setBuzonInterno(buzonInternoCreado.getId());
            if(d.getEmpresa() != null) {
                bid.setEmpresa(d.getEmpresa().getId());
            }
            if(d.getUsuario() != null) {
                bid.setUsuario(d.getUsuario().getId());
            }
            daoHelper.fulfillAuditorFields(true, bid, usuarioDto.getId());
            buzonInternoDestinatarioRepository.save(bid);

            try {
                //notificacionEmailService.enviarEmail(NotificacionEmailEnum.EMPRESA_REGISTRADA, empresaDto, daoToDtoConverter.convertDaoToDtoUser(usuario), empresaDto.getUsuario());
            } catch(Exception ex) {
                logger.warn("El correo no se ha podido enviar. Motivo: {}", ex);
            }
        });

        return daoToDtoConverter.convertDaoToDtoBuzonInterno(buzonInternoCreado);
    }

    @Override
    public BuzonInternoDto modificarBuzonInterno(String uuid, BuzonInternoDto buzonInternoDto, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || buzonInternoDto == null) {
            logger.warn("Alguno de los parametros es invalido o nulo");
            throw new InvalidDataException();
        }

        logger.info("Modificando el mensaje en el buzon interno con uuid [{}]", uuid);
        BuzonInterno buzonInterno = buzonInternoRepository.getByUuidAndEliminadoFalse(uuid);
        if(buzonInterno == null) {
            logger.warn("El mensaje no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        buzonInterno.setMensaje(buzonInternoDto.getMensaje());
        buzonInterno.setMotivo(buzonInternoDto.getMotivo());
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        daoHelper.fulfillAuditorFields(false, buzonInterno, usuarioDto.getId());
        buzonInternoRepository.save(buzonInterno);

        return buzonInternoDto;
    }

    @Override
    public BuzonInternoDto eliminarBuzonInterno(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el mensaje en el buzon interno con uuid [{}]", uuid);
        BuzonInterno buzonInterno = buzonInternoRepository.getByUuidAndEliminadoFalse(uuid);
        if(buzonInterno == null) {
            logger.warn("El mensaje no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        buzonInterno.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, buzonInterno, usuarioDto.getId());
        buzonInternoRepository.save(buzonInterno);

        return daoToDtoConverter.convertDaoToDtoBuzonInterno(buzonInterno);
    }
}
