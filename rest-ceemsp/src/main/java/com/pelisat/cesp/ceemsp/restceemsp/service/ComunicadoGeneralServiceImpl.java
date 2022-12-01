package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ComunicadoGeneralDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.ComunicadoGeneral;
import com.pelisat.cesp.ceemsp.database.repository.ComunicadoGeneralRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComunicadoGeneralServiceImpl implements ComunicadoGeneralService {

    private final Logger logger = LoggerFactory.getLogger(ComunicadoGeneralService.class);
    private final UsuarioService usuarioService;
    private final ComunicadoGeneralRepository comunicadoGeneralRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public ComunicadoGeneralServiceImpl(UsuarioService usuarioService, ComunicadoGeneralRepository comunicadoGeneralRepository,
                                        DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                        DaoHelper<CommonModel> daoHelper) {
        this.usuarioService = usuarioService;
        this.comunicadoGeneralRepository = comunicadoGeneralRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<ComunicadoGeneralDto> obtenerComunicadosGenerales(String titulo, Integer mes, Integer ano) {
        logger.info("Obteniendo los comunicados generales");
        List<ComunicadoGeneral> comunicadosGenerales = new ArrayList<ComunicadoGeneral>();
        if(mes != null && ano != null) {
            if(StringUtils.isNotBlank(titulo)) {
                comunicadosGenerales = comunicadoGeneralRepository.getAllByFechaActualizacionAndTitulo(ano, mes, titulo);
            } else {
                comunicadosGenerales = comunicadoGeneralRepository.getAllByFechaActualizacion(ano, mes);
            }
        } else {
            comunicadosGenerales = comunicadoGeneralRepository.getAllByEliminadoFalseOrderByFechaPublicacionDesc();

        }
        return comunicadosGenerales.stream().map(daoToDtoConverter::convertDaoToDtoComunicadoGeneral).collect(Collectors.toList());
    }

    @Override
    public ComunicadoGeneralDto obtenerComunicadoPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid del comunicado a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }
        logger.info("Obteniendo el comunicado con el uuid [{}]", uuid);

        ComunicadoGeneral comunicadoGeneral = comunicadoGeneralRepository.getByUuidAndEliminadoFalse(uuid);

        if(comunicadoGeneral == null) {
            logger.warn("El comunicado con el uuid [{}]", uuid);
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoComunicadoGeneral(comunicadoGeneral);
    }

    @Override
    public ComunicadoGeneralDto obtenerUltimoComunicado() {
        logger.info("Obteniendo el ultimo comunicado creado");

        List<ComunicadoGeneral> comunicadoGenerales = comunicadoGeneralRepository.getTop1ByFechaPublicacionBeforeAndEliminadoFalseOrderByFechaPublicacionDesc(LocalDate.now().plusDays(1));
        if(comunicadoGenerales != null && comunicadoGenerales.size() > 0) {
            return daoToDtoConverter.convertDaoToDtoComunicadoGeneral(comunicadoGenerales.get(0));
        } else {
            return null;
        }
        //return daoToDtoConverter.convertDaoToDtoComunicadoGeneral(comunicadoGenerales.get(0));
    }

    @Override
    public ComunicadoGeneralDto guardarComunicado(String username, ComunicadoGeneralDto comunicadoGeneralDto) {
        if(StringUtils.isBlank(username) || comunicadoGeneralDto == null) {
            logger.warn("El usuario o el comunicado general vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando el comunicado general");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        ComunicadoGeneral comunicadoGeneral = dtoToDaoConverter.convertDtoToDaoConverter(comunicadoGeneralDto);
        comunicadoGeneral.setFechaPublicacion(LocalDate.parse(comunicadoGeneralDto.getFechaPublicacion()));
        daoHelper.fulfillAuditorFields(true, comunicadoGeneral, usuarioDto.getId());

        ComunicadoGeneral comunicadoGeneralCreado = comunicadoGeneralRepository.save(comunicadoGeneral);
        return daoToDtoConverter.convertDaoToDtoComunicadoGeneral(comunicadoGeneralCreado);
    }

    @Override
    public ComunicadoGeneralDto modificarComunicado(String uuid, String username, ComunicadoGeneralDto comunicadoGeneralDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || comunicadoGeneralDto == null) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }
        logger.info("Modificando el comunicado con el uuid [{}]", uuid);

        ComunicadoGeneral comunicadoGeneral = comunicadoGeneralRepository.getByUuidAndEliminadoFalse(uuid);

        if(comunicadoGeneral == null) {
            logger.warn("El comunicado con el uuid no existe [{}]", uuid);
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        comunicadoGeneral.setTitulo(comunicadoGeneralDto.getTitulo());
        comunicadoGeneral.setFechaPublicacion(LocalDate.parse(comunicadoGeneralDto.getFechaPublicacion()));
        comunicadoGeneral.setDescripcion(comunicadoGeneralDto.getDescripcion());
        daoHelper.fulfillAuditorFields(false, comunicadoGeneral, usuarioDto.getId());
        comunicadoGeneralRepository.save(comunicadoGeneral);
        return comunicadoGeneralDto;
    }

    @Override
    public ComunicadoGeneralDto eliminarComunicado(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }
        logger.info("Modificando el comunicado con el uuid [{}]", uuid);

        ComunicadoGeneral comunicadoGeneral = comunicadoGeneralRepository.getByUuidAndEliminadoFalse(uuid);

        if(comunicadoGeneral == null) {
            logger.warn("El comunicado con el uuid no existe [{}]", uuid);
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        comunicadoGeneral.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, comunicadoGeneral, usuarioDto.getId());
        comunicadoGeneralRepository.save(comunicadoGeneral);
        return daoToDtoConverter.convertDaoToDtoComunicadoGeneral(comunicadoGeneral);
    }
}
