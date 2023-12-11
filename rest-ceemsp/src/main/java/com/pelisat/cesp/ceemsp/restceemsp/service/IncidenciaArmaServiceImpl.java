package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
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

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class IncidenciaArmaServiceImpl implements IncidenciaArmaService {

    private final Logger logger = LoggerFactory.getLogger(IncidenciaPersonalService.class);
    private final IncidenciaArmaRepository incidenciaArmaRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final ArmaRepository armaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final PersonaRepository personaRepository;
    private final PersonalArmaRepository personalArmaRepository;

    @Autowired
    public IncidenciaArmaServiceImpl(IncidenciaArmaRepository incidenciaArmaRepository, IncidenciaRepository incidenciaRepository,
                                         DaoToDtoConverter daoToDtoConverter, UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper,
                                         ArmaRepository armaRepository, PersonaRepository personaRepository, PersonalArmaRepository personalArmaRepository) {
        this.incidenciaArmaRepository = incidenciaArmaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.armaRepository = armaRepository;
        this.personaRepository = personaRepository;
        this.personalArmaRepository = personalArmaRepository;
    }

    @Override
    public List<ArmaDto> obtenerArmasIncidencia(String empresaUuid, String incidenciaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando arma a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<IncidenciaArma> incidenciaArmas = incidenciaArmaRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());

        return incidenciaArmas.stream().map(a -> {
            Arma arma = armaRepository.getOne(a.getArma());
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setRazonBajaIncidencia(a.getMotivoEliminacion());
            return armaDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ArmaDto> obtenerArmasEliminadasIncidencia(String empresaUuid, String incidenciaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando arma a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<IncidenciaArma> incidenciaArmas = incidenciaArmaRepository.getAllByIncidenciaAndEliminadoTrue(incidencia.getId());

        return incidenciaArmas.stream().map(a -> {
            Arma arma = armaRepository.getOne(a.getArma());
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setRazonBajaIncidencia(a.getMotivoEliminacion());
            return armaDto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ArmaDto agregarArmaIncidencia(String empresaUuid, String incidenciaUuid, String username, ArmaDto armaDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || armaDto == null) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando arma a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        Arma arma = armaRepository.getOne(armaDto.getId());

        if(arma == null) {
            logger.warn("El arma no existe en la base de datos");
            throw new InvalidDataException();
        }

        if(arma.getStatus() == ArmaStatusEnum.ASIGNADA) {
            Personal personaConArma = null;
            if(arma.getTipo() == ArmaTipoEnum.CORTA) {
                personaConArma = personaRepository.getByArmaCortaAndEliminadoFalse(arma.getId());
                personaConArma.setArmaCorta(null);
            } else if(arma.getTipo() == ArmaTipoEnum.LARGA) {
                personaConArma = personaRepository.getByArmaLargaAndEliminadoFalse(arma.getId());
                personaConArma.setArmaLarga(null);
            }

            List<PersonalArma> personalArmas = personalArmaRepository.getAllByPersonalAndEliminadoFalse(personaConArma.getId());
            PersonalArma armaEncontrada = null;

            for(PersonalArma pa : personalArmas) {
                Arma armaTemporal = armaRepository.getOne(pa.getArma());
                if(armaTemporal.getTipo() == armaDto.getTipo()) {
                    armaEncontrada = pa;
                }
            }

            if(armaEncontrada != null) {
                armaEncontrada.setEliminado(true);
                armaEncontrada.setMotivoBajaAsignacion("Esta arma se ha desasignado al elemento por involucracion en la incidencia: " + incidencia.getNumero() + " registrada a las " + LocalDate.now());
                daoHelper.fulfillAuditorFields(true, armaEncontrada, usuario.getId());
                personalArmaRepository.save(armaEncontrada);
            }

            daoHelper.fulfillAuditorFields(false, personaConArma, usuario.getId());
            personaRepository.save(personaConArma);
        }


        arma.setStatus(armaDto.getStatus());
        arma.setEliminado(true);
        arma.setMotivoBaja(armaDto.getStatus().toString());
        arma.setFechaBaja(LocalDate.now());
        arma.setObservacionesBaja("Se ha eliminado el arma temporalmente con status " + armaDto.getStatus().toString() + " por el reporte de incidencia " + incidencia.getNumero() + " registrada a las " + LocalDate.now());
        daoHelper.fulfillAuditorFields(false, arma, usuario.getId());
        armaRepository.save(arma);

        IncidenciaArma incidenciaArma = new IncidenciaArma();
        incidenciaArma.setIncidencia(incidencia.getId());
        incidenciaArma.setArma(armaDto.getId());
        incidenciaArma.setStatus(armaDto.getStatus());
        daoHelper.fulfillAuditorFields(true, incidenciaArma, usuario.getId());

        incidenciaArmaRepository.save(incidenciaArma);

        return armaDto;
    }

    @Override
    @Transactional
    public ArmaDto eliminarArmaIncidencia(String empresaUuid, String incidenciaUuid, String armaIncidenciaUuid, String username, ArmaDto armaDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(armaIncidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando arma de la incidencia con el uuid [{}]", armaIncidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Arma arma = armaRepository.getByUuid(armaIncidenciaUuid);

        if(arma == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        IncidenciaArma incidenciaArma = incidenciaArmaRepository.getByIncidenciaAndArmaAndEliminadoFalse(incidencia.getId(), arma.getId());

        if(incidenciaArma == null) {
            logger.warn("El arma en la incidencia no se encuentra registrada");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        arma.setStatus(ArmaStatusEnum.DEPOSITO);
        arma.setMotivoBaja(null);
        arma.setFechaBaja(null);
        arma.setObservacionesBaja(null);
        arma.setEliminado(false);
        daoHelper.fulfillAuditorFields(false, arma, usuario.getId());
        armaRepository.save(arma);

        incidenciaArma.setEliminado(true);
        incidenciaArma.setMotivoEliminacion(armaDto.getRazonBajaIncidencia());
        daoHelper.fulfillAuditorFields(false, incidenciaArma, usuario.getId());
        incidenciaArmaRepository.save(incidenciaArma);

        return null;
    }
}
