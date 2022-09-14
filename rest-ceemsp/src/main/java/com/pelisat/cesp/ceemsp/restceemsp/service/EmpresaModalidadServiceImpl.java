package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.EmpresaLicenciaColectiva;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaModalidadServiceImpl implements EmpresaModalidadService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final EmpresaModalidadRepository empresaModalidadRepository;
    private final ModalidadService modalidadService;
    private final SubmodalidadService submodalidadService;

    private final Logger logger = LoggerFactory.getLogger(EmpresaModalidadService.class);

    @Autowired
    public EmpresaModalidadServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                       DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                       EmpresaService empresaService, EmpresaModalidadRepository empresaModalidadRepository,
                                       ModalidadService modalidadService, SubmodalidadService submodalidadService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.empresaModalidadRepository = empresaModalidadRepository;
        this.submodalidadService = submodalidadService;
        this.modalidadService = modalidadService;
    }

    @Override
    public List<EmpresaModalidadDto> obtenerModalidadesEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        List<EmpresaModalidad> empresaModalidades = empresaModalidadRepository
                .findAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        List<EmpresaModalidadDto> response = empresaModalidades.stream().map(em -> {
            EmpresaModalidadDto empresaModalidadDto = daoToDtoConverter.convertDaoToDtoEmpresaModalidad(em);
            empresaModalidadDto.setModalidad(modalidadService.obtenerModalidadPorId(em.getModalidad()));
            if(em.getSubmodalidad() != null && em.getSubmodalidad() > 0 ){
                empresaModalidadDto.setSubmodalidad(submodalidadService.obtenerSubmodalidadPorId(em.getSubmodalidad()));
            }
            return empresaModalidadDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public EmpresaModalidadDto guardarModalidad(String empresaUuid, String username, EmpresaModalidadDto empresaModalidadDto) {
        if(StringUtils.isBlank(empresaUuid) || empresaModalidadDto == null || StringUtils.isBlank(username)) {
            logger.warn("El uuid o la escritura a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaModalidad empresaModalidad = new EmpresaModalidad();

        empresaModalidad.setUuid(RandomStringUtils.randomAlphanumeric(12));
        empresaModalidad.setEmpresa(empresaDto.getId());
        empresaModalidad.setModalidad(empresaModalidadDto.getModalidad().getId());
        empresaModalidad.setNumeroRegistroFederal(empresaModalidadDto.getNumeroRegistroFederal());
        if(empresaModalidadDto.getSubmodalidad() != null) {
            empresaModalidad.setSubmodalidad(empresaModalidadDto.getSubmodalidad().getId());
        }

        if(StringUtils.isNotBlank(empresaModalidadDto.getFechaInicio())) {
            empresaModalidad.setFechaInicio(LocalDate.parse(empresaModalidadDto.getFechaInicio()));
        }

        if(StringUtils.isNotBlank(empresaModalidadDto.getFechaFin())) {
            empresaModalidad.setFechaFin(LocalDate.parse(empresaModalidadDto.getFechaFin()));
        }

        daoHelper.fulfillAuditorFields(true, empresaModalidad, usuarioDto.getId());

        EmpresaModalidad empresaModalidadCreada = empresaModalidadRepository.save(empresaModalidad);

        return daoToDtoConverter.convertDaoToDtoEmpresaModalidad(empresaModalidadCreada);
    }

    @Override
    public EmpresaModalidadDto obtenerModalidadPorUuid(String empresaUuid, String modalidadUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(modalidadUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaModalidad empresaModalidad = empresaModalidadRepository.findByUuidAndEliminadoFalse(modalidadUuid);

        if(empresaModalidad == null) {
            logger.warn("La escritura no existe con el uuid [{}]", modalidadUuid);
            throw new NotFoundResourceException();
        }

        if(empresaModalidad.getEmpresa() != empresaDto.getId()) {
            logger.warn("La escritura no pertenece a la empresa");
            throw new MissingRelationshipException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaModalidad(empresaModalidad);
    }

    @Override
    public EmpresaModalidadDto eliminarModalidadPorUuid(String empresaUuid, String modalidadUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(modalidadUuid) || StringUtils.isBlank(username)) {
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
