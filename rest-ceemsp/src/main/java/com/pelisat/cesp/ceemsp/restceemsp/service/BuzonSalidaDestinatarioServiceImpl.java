package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDestinatarioDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.BuzonInterno;
import com.pelisat.cesp.ceemsp.database.model.BuzonInternoDestinatario;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoDestinatarioRepository;
import com.pelisat.cesp.ceemsp.database.repository.BuzonInternoRepository;
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

@Service
public class BuzonSalidaDestinatarioServiceImpl implements BuzonSalidaDestinatarioService {
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository;
    private final BuzonInternoRepository buzonInternoRepository;
    private final Logger logger = LoggerFactory.getLogger(BuzonSalidaDestinatarioServiceImpl.class);

    @Autowired
    public BuzonSalidaDestinatarioServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                          DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService, BuzonInternoDestinatarioRepository buzonInternoDestinatarioRepository,
                                              BuzonInternoRepository buzonInternoRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.buzonInternoRepository = buzonInternoRepository;
        this.buzonInternoDestinatarioRepository = buzonInternoDestinatarioRepository;
    }


    @Override
    public BuzonInternoDestinatarioDto agregarDestinatario(String buzonInternoUuid, String username, BuzonInternoDestinatarioDto buzonInternoDestinatarioDto) {
        if(StringUtils.isBlank(buzonInternoUuid) || StringUtils.isBlank(username) || buzonInternoDestinatarioDto == null) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando destinatario en el buzon con uuid [{}]", buzonInternoUuid);
        BuzonInterno buzonInterno = buzonInternoRepository.getByUuidAndEliminadoFalse(buzonInternoUuid);

        if(buzonInterno == null) {
            logger.warn("La notificacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        BuzonInternoDestinatario buzonInternoDestinatario = dtoToDaoConverter.convertDtoToDaoBuzonInternoDestinatario(buzonInternoDestinatarioDto);
        buzonInternoDestinatario.setBuzonInterno(buzonInterno.getId());
        daoHelper.fulfillAuditorFields(true, buzonInternoDestinatario, usuarioDto.getId());

        if(buzonInternoDestinatario.getEmpresa() != null) {
            buzonInternoDestinatario.setEmpresa(buzonInternoDestinatarioDto.getEmpresa().getId());
        }
        if(buzonInternoDestinatario.getUsuario() != null) {
            buzonInternoDestinatario.setUsuario(buzonInternoDestinatarioDto.getUsuario().getId());
        }

        BuzonInternoDestinatario destinatarioCreado = buzonInternoDestinatarioRepository.save(buzonInternoDestinatario);
        return daoToDtoConverter.convertDaoToDtoBuzonInternoDestinatario(destinatarioCreado);
    }

    @Override
    public BuzonInternoDestinatarioDto modificarDestinatario(String buzonInternoUuid, String destinatarioUuid, String username, BuzonInternoDestinatarioDto buzonInternoDestinatarioDto) {
        if(StringUtils.isBlank(buzonInternoUuid) || StringUtils.isBlank(destinatarioUuid) || StringUtils.isBlank(username) || buzonInternoDestinatarioDto == null) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("");
        BuzonInternoDestinatario buzonInternoDestinatario = buzonInternoDestinatarioRepository.getByUuidAndEliminadoFalse(destinatarioUuid);

        if(buzonInternoDestinatario == null) {
            logger.warn("El destinatario no existe en la base de datos");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        buzonInternoDestinatario.setTipoDestinatario(buzonInternoDestinatarioDto.getTipoDestinatario());
        buzonInternoDestinatario.setUsuario(null);
        buzonInternoDestinatario.setEmpresa(null);
        buzonInternoDestinatario.setEmail(buzonInternoDestinatarioDto.getEmail());

        if(buzonInternoDestinatarioDto.getEmpresa() != null) {
            buzonInternoDestinatario.setEmpresa(buzonInternoDestinatarioDto.getEmpresa().getId());
        }
        if(buzonInternoDestinatarioDto.getUsuario() != null) {
            buzonInternoDestinatario.setUsuario(buzonInternoDestinatarioDto.getUsuario().getId());
        }

        daoHelper.fulfillAuditorFields(false, buzonInternoDestinatario, usuarioDto.getId());
        buzonInternoDestinatarioRepository.save(buzonInternoDestinatario);

        return daoToDtoConverter.convertDaoToDtoBuzonInternoDestinatario(buzonInternoDestinatario);
    }

    @Override
    public BuzonInternoDestinatarioDto eliminarDestinatario(String buzonInternoUuid, String destinatarioUuid, String username) {
        if(StringUtils.isBlank(buzonInternoUuid) || StringUtils.isBlank(destinatarioUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el destinatario con el uuid [{}]", destinatarioUuid);
        BuzonInternoDestinatario buzonInternoDestinatario = buzonInternoDestinatarioRepository.getByUuidAndEliminadoFalse(destinatarioUuid);

        if(buzonInternoDestinatario == null) {
            logger.warn("El destinatario no existe en la base de datos");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        buzonInternoDestinatario.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, buzonInternoDestinatario, usuarioDto.getId());
        buzonInternoDestinatarioRepository.save(buzonInternoDestinatario);

        return daoToDtoConverter.convertDaoToDtoBuzonInternoDestinatario(buzonInternoDestinatario);
    }
}
