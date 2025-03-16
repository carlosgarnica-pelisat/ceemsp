package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDestinatarioDto;
import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoDestinatarioRepository;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.NotificacionRepository;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoDestinatarioEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.EmailService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuzonSalidaServiceImpl implements BuzonSalidaService {

    private final Logger logger = LoggerFactory.getLogger(BuzonSalidaService.class);
    private final BuzonInternoRepository buzonInternoRepository;
    private final BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository;
    private final EmailService emailService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final EmpresaRepository empresaRepository;
    private final PublicService publicService;
    private final NotificacionRepository notificacionRepository;

    @Autowired
    public BuzonSalidaServiceImpl(BuzonInternoRepository buzonInternoRepository, EmailService emailService,
                                  DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                  UsuarioService usuarioService, BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository,
                                  EmpresaRepository empresaRepository, PublicService publicService, NotificacionRepository notificacionRepository) {
        this.buzonInternoRepository = buzonInternoRepository;
        this.emailService = emailService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaRepository = empresaRepository;
        this.buzonInternoDestinatarioRepository = buzonInternoDestinatarioRepository;
        this.publicService = publicService;
        this.notificacionRepository = notificacionRepository;
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
                destinatario.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(d.getEmpresa())));
            } else if(d.getTipoDestinatario() == TipoDestinatarioEnum.USUARIO) {
                destinatario.setUsuario(usuarioService.getUserById(d.getUsuario()));
            }

            return destinatario;
        }).collect(Collectors.toList()));

        return buzonInternoDto;
    }

    @Transactional
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

                try {
                    Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(d.getEmpresa().getUuid());
                    Map<String, CommonModel> mapaCorreo = new HashMap<>();

                    Notificacion notificacion = new Notificacion();
                    notificacion.setTipo(NotificacionEmailEnum.EMPRESA_REGISTRADA);
                    notificacion.setEmpresa(empresa.getId());
                    notificacion.setNumero("CESP/DSSP/AVISO/" + publicService.buscarProximoNumeroNotificacion() + "/" + LocalDate.now().getYear());
                    String cadenaOriginal = generarCadenaOriginalNotificacion(notificacion, empresa);
                    notificacion.setCadenaOriginal(cadenaOriginal);
                    notificacion.setSelloSalt(RandomStringUtils.randomAlphanumeric(10));

                    // Generando el sello del acuse
                    MessageDigest digest = MessageDigest.getInstance("SHA-512");
                    digest.reset();
                    digest.update(notificacion.getSelloSalt().getBytes(StandardCharsets.UTF_8));
                    byte[] bytes = digest.digest(cadenaOriginal.getBytes(StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();

                    for(int i = 0; i < bytes.length; i++) {
                        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    notificacion.setSello(sb.toString());
                    daoHelper.fulfillAuditorFields(true, notificacion, usuarioDto.getId());
                    Notificacion notificacionCreada = notificacionRepository.save(notificacion);

                    mapaCorreo.put("empresa", empresa);
                    mapaCorreo.put("notificacion", notificacionCreada);
                    emailService.sendEmail(NotificacionEmailEnum.NOTIFICACION, d.getEmail(), mapaCorreo);
                } catch(Exception ex) {
                    logger.warn("El correo no se ha podido enviar. Motivo: {}", ex);
                }
            }
            if(d.getUsuario() != null) {
                bid.setUsuario(d.getUsuario().getId());
            }
            daoHelper.fulfillAuditorFields(true, bid, usuarioDto.getId());
            buzonInternoDestinatarioRepository.save(bid);
        });

        return daoToDtoConverter.convertDaoToDtoBuzonInterno(buzonInternoCreado);
    }

    @Override
    @Transactional
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
    @Transactional
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


    private String generarCadenaOriginalNotificacion(Notificacion notificacion, Empresa empresa) {
        return "||" +
                empresa.getId() + "|" +
                empresa.getUuid() + "|" +
                empresa.getRfc() + "|" +
                empresa.getRegistro() + "|" +
                notificacion.getUuid() + "|" +
                notificacion.getNumero() + "|" +
                notificacion.getTipo() + "||";
    }
}
