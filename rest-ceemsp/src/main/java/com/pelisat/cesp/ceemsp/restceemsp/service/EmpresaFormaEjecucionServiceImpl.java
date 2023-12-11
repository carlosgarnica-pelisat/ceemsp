package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.EmpresaFormaEjecucion;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaFormaEjecucionRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class  EmpresaFormaEjecucionServiceImpl implements EmpresaFormaEjecucionService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaFormaEjecucionServiceImpl.class);
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaRepository empresaRepository;

    @Autowired
    public EmpresaFormaEjecucionServiceImpl(EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository,
                                            UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter,
                                            DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                            EmpresaRepository empresaRepository) {
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaRepository = empresaRepository;
    }

    @Override
    public List<EmpresaFormaEjecucionDto> obtenerFormasEjecucionPorEmpresaUuid(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las formas de ejecucion para la empresa [{}]", empresaUuid);
        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);

        List<EmpresaFormaEjecucion> empresaFormasEjecucion = empresaFormaEjecucionRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());

        return empresaFormasEjecucion.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaFormaEjecucion)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmpresaFormaEjecucionDto crearFormaEjecucion(String empresaUuid, String username, EmpresaFormaEjecucionDto empresaFormaEjecucionDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || empresaFormaEjecucionDto == null) {
            logger.warn("La forma de ejecucion, la empresa o el usuario vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando una nueva de ejecucion para la empresa con el uuid [{}]", empresaUuid);

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaFormaEjecucion empresaFormaEjecucion = dtoToDaoConverter.convertDtoToDaoEmpresaFormaEjecucion(empresaFormaEjecucionDto);
        daoHelper.fulfillAuditorFields(true, empresaFormaEjecucion, usuarioDto.getId());
        empresaFormaEjecucion.setEmpresa(empresa.getId());

        EmpresaFormaEjecucion formaejecucionCreada = empresaFormaEjecucionRepository.save(empresaFormaEjecucion);

        if(!empresa.isFormasEjecucionCapturadas()) {
            empresa.setFormasEjecucionCapturadas(true);
            daoHelper.fulfillAuditorFields(false, empresa, usuarioDto.getId());
            empresaRepository.save(empresa);
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaFormaEjecucion(formaejecucionCreada);
    }

    @Transactional
    @Override
    public EmpresaFormaEjecucionDto eliminarFormaEjecucion(String empresaUuid, String formaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(formaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaFormaEjecucion empresaFormaEjecucion = empresaFormaEjecucionRepository.findByUuidAndEliminadoFalse(formaUuid);
        if(empresaFormaEjecucion == null) {
            logger.warn("La forma de ejecucion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        logger.info("Eliminando la forma de ejecucion con el uuid [{}]", formaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        empresaFormaEjecucion.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaFormaEjecucion, usuarioDto.getId());

        empresaFormaEjecucionRepository.save(empresaFormaEjecucion);

        int count = empresaFormaEjecucionRepository.countAllByEmpresaAndEliminadoFalse(empresa.getId());

        if(count < 1) {
            empresa.setFormasEjecucionCapturadas(false);
            daoHelper.fulfillAuditorFields(false, empresa, usuarioDto.getId());
            empresaRepository.save(empresa);
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaFormaEjecucion(empresaFormaEjecucion);
    }
}
