package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraSocioRepository;
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
public class EmpresaEscrituraSocioServiceImpl implements EmpresaEscrituraSocioService {

    private final EmpresaEscrituraSocioRepository empresaEscrituraSocioRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraSocioService.class);

    @Autowired
    public EmpresaEscrituraSocioServiceImpl(EmpresaEscrituraSocioRepository empresaEscrituraSocioRepository, EmpresaEscrituraRepository empresaEscrituraRepository,
                                            UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                            DaoHelper<CommonModel> daoHelper, EmpresaService empresaService) {
        this.empresaEscrituraSocioRepository = empresaEscrituraSocioRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
    }

    @Override
    public List<EmpresaEscrituraSocioDto> obtenerSociosPorEscritura(String empresaUuid, String escrituraUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaEscrituraSocio> empresaEscrituraSocios = empresaEscrituraSocioRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

        return empresaEscrituraSocios.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraSocio)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraSocioDto obtenerSocioPorUuid(String empresaUuid, String escrituraUuid, String socioUuid, boolean soloEntidad) {
        return null;
    }

    @Override
    public EmpresaEscrituraSocioDto crearSocio(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto) {
        if(empresaEscrituraSocioDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El socio, la empresa o la escritura estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando socio en la escritura: [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraSocio empresaEscrituraSocio = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraSocio(empresaEscrituraSocioDto);
        empresaEscrituraSocio.setEscritura(empresaEscritura.getId());
        daoHelper.fulfillAuditorFields(true, empresaEscrituraSocio, usuario.getId());
        EmpresaEscrituraSocio empresaEscrituraSocioCreado = empresaEscrituraSocioRepository.save(empresaEscrituraSocio);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraSocio(empresaEscrituraSocioCreado);
    }

    @Override
    public EmpresaEscrituraSocioDto modificarSocio(String empresaUuid, String escrituraUuid, String representanteUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(representanteUuid) || empresaEscrituraSocioDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta modificando el socio de la escritura con el uuid [{}]", representanteUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraSocio empresaEscrituraSocio = empresaEscrituraSocioRepository.findByUuidAndEliminadoFalse(representanteUuid);

        if(empresaEscrituraSocio == null) {
            logger.warn("El socio a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraSocio.setNombres(empresaEscrituraSocioDto.getNombres());
        empresaEscrituraSocio.setApellidos(empresaEscrituraSocioDto.getApellidos());
        empresaEscrituraSocio.setApellidoMaterno(empresaEscrituraSocioDto.getApellidoMaterno());
        empresaEscrituraSocio.setCurp(empresaEscrituraSocioDto.getCurp());
        empresaEscrituraSocio.setSexo(empresaEscrituraSocioDto.getSexo());
        empresaEscrituraSocio.setPorcentajeAcciones(empresaEscrituraSocioDto.getPorcentajeAcciones());
        daoHelper.fulfillAuditorFields(false, empresaEscrituraSocio, usuario.getId());
        empresaEscrituraSocioRepository.save(empresaEscrituraSocio);

        return empresaEscrituraSocioDto;
    }

    @Override
    public EmpresaEscrituraSocioDto eliminarSocio(String empresaUuid, String escrituraUuid, String representanteUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(representanteUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el socio con el ID [{}]", representanteUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraSocio empresaEscrituraSocio = empresaEscrituraSocioRepository.findByUuidAndEliminadoFalse(representanteUuid);

        if(empresaEscrituraSocio == null) {
            logger.warn("El socio a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraSocio.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaEscrituraSocio, usuario.getId());
        empresaEscrituraSocioRepository.save(empresaEscrituraSocio);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraSocio(empresaEscrituraSocio);
    }
}
