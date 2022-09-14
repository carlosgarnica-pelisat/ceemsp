package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.Incidencia;
import com.pelisat.cesp.ceemsp.database.model.IncidenciaCan;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaCanRepository;
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

@Service
public class EmpresaIncidenciaCanServiceImpl implements EmpresaIncidenciaCanService {
    private final Logger logger = LoggerFactory.getLogger(EmpresaIncidenciaCanService.class);
    private final IncidenciaCanRepository incidenciaCanRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final CanRepository canRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<IncidenciaCan> daoHelper;

    @Autowired
    public EmpresaIncidenciaCanServiceImpl(
            IncidenciaCanRepository incidenciaCanRepository, IncidenciaRepository incidenciaRepository,
            CanRepository canRepository, DaoToDtoConverter daoToDtoConverter,
            UsuarioService usuarioService, DaoHelper<IncidenciaCan> daoHelper
    ) {
        this.incidenciaCanRepository = incidenciaCanRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.canRepository = canRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
    }


    @Override
    public CanDto agregarCanIncidencia(String incidenciaUuid, String username, CanDto canDto) {
        if(StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || canDto == null) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando can a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        IncidenciaCan incidenciaCan = new IncidenciaCan();
        incidenciaCan.setIncidencia(incidencia.getId());
        incidenciaCan.setCan(canDto.getId());
        daoHelper.fulfillAuditorFields(true, incidenciaCan, usuario.getId());

        incidenciaCanRepository.save(incidenciaCan);

        return canDto;
    }

    @Override
    public CanDto eliminarCanIncidencia(String incidenciaUuid, String canIncidenciaUuid, String username) {
        if(StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(canIncidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando can de la incidencia con el uuid [{}]", canIncidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Can can = canRepository.getByUuidAndEliminadoFalse(canIncidenciaUuid);

        if(can == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        IncidenciaCan incidenciaCan = incidenciaCanRepository.getByIncidenciaAndCanAndEliminadoFalse(can.getId(), incidencia.getId());

        if(incidenciaCan == null) {
            logger.warn("El arma en la incidencia no se encuentra registrada");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        incidenciaCan.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, incidenciaCan, usuario.getId());
        incidenciaCanRepository.save(incidenciaCan);

        return null;
    }
}
