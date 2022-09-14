package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaModalidad;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaModalidadRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaModalidadServiceImpl implements EmpresaModalidadService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaModalidadRepository empresaModalidadRepository;
    private final CatalogoService catalogoService;
    private final UsuarioService usuarioService;

    private final Logger logger = LoggerFactory.getLogger(EmpresaModalidadService.class);

    @Autowired
    public EmpresaModalidadServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                       DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                       EmpresaService empresaService, EmpresaModalidadRepository empresaModalidadRepository,
                                       CatalogoService catalogoService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaModalidadRepository = empresaModalidadRepository;
        this.catalogoService = catalogoService;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<EmpresaModalidadDto> obtenerModalidadesEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        List<EmpresaModalidad> empresaModalidades = empresaModalidadRepository
                .findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        List<EmpresaModalidadDto> response = empresaModalidades.stream().map(em -> {
            EmpresaModalidadDto empresaModalidadDto = daoToDtoConverter.convertDaoToDtoEmpresaModalidad(em);
            empresaModalidadDto.setModalidad(catalogoService.obtenerModalidadPorId(em.getModalidad()));
            if(em.getSubmodalidad() != null && em.getSubmodalidad() > 0 ){
                empresaModalidadDto.setSubmodalidad(catalogoService.obtenerSubmodalidadPorId(em.getSubmodalidad()));
            }
            return empresaModalidadDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public EmpresaModalidadDto guardarModalidad(String username, EmpresaModalidadDto empresaModalidadDto) {
        if(empresaModalidadDto == null || StringUtils.isBlank(username)) {
            logger.warn("El uuid o la escritura a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaModalidad empresaModalidad = new EmpresaModalidad();

        empresaModalidad.setUuid(RandomStringUtils.randomAlphanumeric(12));
        empresaModalidad.setEmpresa(usuarioDto.getEmpresa().getId());
        empresaModalidad.setModalidad(empresaModalidadDto.getModalidad().getId());
        if(empresaModalidadDto.getSubmodalidad() != null) {
            empresaModalidad.setSubmodalidad(empresaModalidadDto.getSubmodalidad().getId());
        }

        daoHelper.fulfillAuditorFields(true, empresaModalidad, usuarioDto.getId());

        EmpresaModalidad empresaModalidadCreada = empresaModalidadRepository.save(empresaModalidad);

        return daoToDtoConverter.convertDaoToDtoEmpresaModalidad(empresaModalidadCreada);
    }

    @Override
    public EmpresaModalidadDto obtenerModalidadPorUuid(String modalidadUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(modalidadUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaModalidad empresaModalidad = empresaModalidadRepository.findByUuidAndEliminadoFalse(modalidadUuid);

        if(empresaModalidad == null) {
            logger.warn("La escritura no existe con el uuid [{}]", modalidadUuid);
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaModalidad(empresaModalidad);
    }

    @Override
    public EmpresaModalidadDto eliminarModalidadPorUuid(String modalidadUuid, String username) {
        if(StringUtils.isBlank(modalidadUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros no es valido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la modalidad de la empresa con uuid [{}]", modalidadUuid);

        EmpresaModalidad empresaModalidad = empresaModalidadRepository.findByUuidAndEliminadoFalse(modalidadUuid);
        if(empresaModalidad == null) {
            logger.info("No existe la modalidad a eliminar");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        empresaModalidad.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaModalidad, usuarioDto.getId());
        empresaModalidadRepository.save(empresaModalidad);
        return daoToDtoConverter.convertDaoToDtoEmpresaModalidad(empresaModalidad);
    }
}
