package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.TipoInfraestructuraDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.TipoInfraestructura;
import com.pelisat.cesp.ceemsp.database.repository.TipoInfraestructuraRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoInfraestructuraServiceImpl implements TipoInfraestructuraService {

    private final UsuarioService usuarioService;
    private final TipoInfraestructuraRepository tipoInfraestructuraRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(TipoInfraestructuraService.class);

    @Autowired
    public TipoInfraestructuraServiceImpl(UsuarioService usuarioService, TipoInfraestructuraRepository tipoInfraestructuraRepository,
                                          DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                          DaoHelper<CommonModel> daoHelper) {
        this.usuarioService = usuarioService;
        this.tipoInfraestructuraRepository = tipoInfraestructuraRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<TipoInfraestructuraDto> obtenerTiposInfraestructura() {
        logger.info("Consultando todos los tipos de infraestructura en la base de datos");
        List<TipoInfraestructura> tipoInfraestructuras = tipoInfraestructuraRepository.getAllByEliminadoFalse();
        return tipoInfraestructuras.stream()
                .map(daoToDtoConverter::convertDaoToDtoTipoInfraestructura)
                .collect(Collectors.toList());
    }

    @Override
    public TipoInfraestructuraDto obtenerTipoInfraestructuraPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid del tipo de infraestructura a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        TipoInfraestructura tipoInfraestructura = tipoInfraestructuraRepository.getByUuidAndEliminadoFalse(uuid);

        if(tipoInfraestructura == null) {
            logger.warn("El tipo de infraestructura con uuid [{}] viene como nula o vacia", uuid);
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoTipoInfraestructura(tipoInfraestructura);
    }

    @Override
    public TipoInfraestructuraDto obtenerTipoInfraestructuraPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id del tipo de infraestructura a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        TipoInfraestructura tipoInfraestructura = tipoInfraestructuraRepository.getOne(id);

        if(tipoInfraestructura == null) {
            logger.warn("El tipo de infraestructura con id [{}] viene como nula o vacia", id);
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoTipoInfraestructura(tipoInfraestructura);
    }

    @Override
    public TipoInfraestructuraDto guardarTipoInfraestructura(TipoInfraestructuraDto tipoInfraestructuraDto, String username) {
        if(tipoInfraestructuraDto == null || StringUtils.isBlank(username)) {
            logger.warn("El tipo de vehiculo o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nuevo tipo de infraestructura con nombre: [{}]", tipoInfraestructuraDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        TipoInfraestructura tipoInfraestructura = dtoToDaoConverter.convertDtoToDaoTipoInfraestructura(tipoInfraestructuraDto);

        daoHelper.fulfillAuditorFields(true, tipoInfraestructura, usuario.getId());

        TipoInfraestructura tipoInfraestructuraCreado = tipoInfraestructuraRepository.save(tipoInfraestructura);

        return daoToDtoConverter.convertDaoToDtoTipoInfraestructura(tipoInfraestructura);
    }
}
