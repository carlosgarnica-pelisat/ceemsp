package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraRepresentante;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraSocio;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepresentanteRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaEscrituraRepresentanteServiceImpl implements EmpresaEscrituraRepresentanteService {

    private final EmpresaEscrituraRepresentanteRepository empresaEscrituraRepresentanteRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraSocioService.class);

    @Autowired
    public EmpresaEscrituraRepresentanteServiceImpl(EmpresaEscrituraRepresentanteRepository empresaEscrituraRepresentanteRepository, EmpresaEscrituraRepository empresaEscrituraRepository,
                                            UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                            DaoHelper<CommonModel> daoHelper, EmpresaService empresaService) {
        this.empresaEscrituraRepresentanteRepository = empresaEscrituraRepresentanteRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
    }

    @Override
    public List<EmpresaEscrituraRepresentanteDto> obtenerRepresentantesPorEscritura(String empresaUuid, String escrituraUuid) {
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

        List<EmpresaEscrituraRepresentante> empresaEscrituraRepresentantes = empresaEscrituraRepresentanteRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

        return empresaEscrituraRepresentantes.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraRepresentante)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraRepresentanteDto obtenerRepresentantePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad) {
        return null;
    }

    @Override
    public EmpresaEscrituraRepresentanteDto crearRepresentante(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto) {
        if(empresaEscrituraRepresentanteDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El representante, la empresa o la escritura estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando representante en la escritura: [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraRepresentante empresaEscrituraRepresentante = dtoToDaoConverter.convertDtoToDaoEmpresaRepresentante(empresaEscrituraRepresentanteDto);
        empresaEscrituraRepresentante.setEscritura(empresaEscritura.getId());
        daoHelper.fulfillAuditorFields(true, empresaEscrituraRepresentante, usuario.getId());
        EmpresaEscrituraRepresentante empresaEscrituraRepresentanteCreada = empresaEscrituraRepresentanteRepository.save(empresaEscrituraRepresentante);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraRepresentante(empresaEscrituraRepresentanteCreada);
    }

    @Override
    public EmpresaEscrituraRepresentanteDto modificarRepresentante(String empresaUuid, String escrituraUuid, String representanteUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(representanteUuid) || empresaEscrituraRepresentanteDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta modificando el representante de la escritura con el uuid [{}]", representanteUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraRepresentante empresaEscrituraRepresentante = empresaEscrituraRepresentanteRepository.findByUuidAndEliminadoFalse(representanteUuid);

        if(empresaEscrituraRepresentante == null) {
            logger.warn("El representante a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraRepresentante.setNombres(empresaEscrituraRepresentanteDto.getNombres());
        empresaEscrituraRepresentante.setApellidos(empresaEscrituraRepresentanteDto.getApellidos());
        empresaEscrituraRepresentante.setApellidoMaterno(empresaEscrituraRepresentanteDto.getApellidoMaterno());
        empresaEscrituraRepresentante.setCurp(empresaEscrituraRepresentanteDto.getCurp());
        empresaEscrituraRepresentante.setSexo(empresaEscrituraRepresentanteDto.getSexo());
        daoHelper.fulfillAuditorFields(false, empresaEscrituraRepresentante, usuario.getId());
        empresaEscrituraRepresentanteRepository.save(empresaEscrituraRepresentante);

        return empresaEscrituraRepresentanteDto;
    }

    @Override
    public EmpresaEscrituraRepresentanteDto eliminarRepresentante(String empresaUuid, String escrituraUuid, String representanteUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(representanteUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el representante con el ID [{}]", representanteUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraRepresentante empresaEscrituraRepresentante = empresaEscrituraRepresentanteRepository.findByUuidAndEliminadoFalse(representanteUuid);

        if(empresaEscrituraRepresentante == null) {
            logger.warn("El representante a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraRepresentante.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaEscrituraRepresentante, usuario.getId());
        empresaEscrituraRepresentanteRepository.save(empresaEscrituraRepresentante);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraRepresentante(empresaEscrituraRepresentante);
    }


}
