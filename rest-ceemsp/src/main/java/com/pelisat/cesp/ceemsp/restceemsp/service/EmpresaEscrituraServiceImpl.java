package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraApoderadoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepresentanteRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraSocioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaEscrituraServiceImpl implements EmpresaEscrituraService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraService.class);
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository;
    private final EmpresaEscrituraRepresentanteRepository empresaEscrituraRepresentanteRepository;
    private final EmpresaEscrituraSocioRepository empresaEscrituraSociosRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public EmpresaEscrituraServiceImpl(
            EmpresaEscrituraRepository empresaEscrituraRepository, EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository,
            EmpresaEscrituraRepresentanteRepository empresaEscrituraRepresentanteRepository, EmpresaService empresaService,
            DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
            UsuarioService usuarioService, EmpresaEscrituraSocioRepository empresaEscrituraSociosRepository
    ) {
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.empresaEscrituraRepresentanteRepository = empresaEscrituraRepresentanteRepository;
        this.empresaEscrituraSociosRepository = empresaEscrituraSociosRepository;
        this.empresaService = empresaService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<EmpresaEscrituraDto> obtenerEscriturasEmpresaPorUuid(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacío");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        List<EmpresaEscritura> empresaEscrituras = empresaEscrituraRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());
        return empresaEscrituras.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscritura)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraDto obtenerEscrituraPorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la escritura con el uuid [{}]", escrituraUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("La escritura no existe con el uuid [{}]", escrituraUuid);
            throw new NotFoundResourceException();
        }

        if(empresaEscritura.getEmpresa() != empresaDto.getId()) {
            logger.warn("La escritura no pertenece a la empresa");
            throw new MissingRelationshipException();
        }

        EmpresaEscrituraDto response = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscritura);

        if(!soloEntidad) {
            logger.info("Descargando informacion de los socios");
            List<EmpresaEscrituraSocio> socios = empresaEscrituraSociosRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraApoderado> apoderados = empresaEscrituraApoderadoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraRepresentante> representantes = empresaEscrituraRepresentanteRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

            response.setSocios(socios.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraSocio).collect(Collectors.toList()));
            response.setApoderados(apoderados.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraApoderado).collect(Collectors.toList()));
            response.setRepresentantes(representantes.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraRepresentante).collect(Collectors.toList()));
        }

        return response;
    }

    @Override
    @Transactional
    public EmpresaEscrituraDto guardarEscritura(String empresaUuid, EmpresaEscrituraDto empresaEscrituraDto,
                                                String username) {
        if(StringUtils.isBlank(empresaUuid) || empresaEscrituraDto == null || StringUtils.isBlank(username)) {
            logger.warn("El uuid o la escritura a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaEscritura empresaEscritura = dtoToDaoConverter.convertDtoToDaoEmpresaEscritura(empresaEscrituraDto);
        empresaEscritura.setEmpresa(empresaDto.getId());
        daoHelper.fulfillAuditorFields(true, empresaEscritura, usuarioDto.getId());

        EmpresaEscritura empresaEscrituraCreada = empresaEscrituraRepository.save(empresaEscritura);

        if(empresaEscrituraDto.getApoderados() != null && empresaEscrituraDto.getApoderados().size() > 0) {
            logger.info("Se encontraron apoderados. Agregando [{}] nuevos apoderados",
                    empresaEscrituraDto.getApoderados().size());
            List<EmpresaEscrituraApoderado> empresaEscrituraApoderados = empresaEscrituraDto.getApoderados()
                    .stream()
                    .map(ap -> {
                        EmpresaEscrituraApoderado eea = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraApoderado(ap);
                        eea.setEscritura(empresaEscrituraCreada.getId());
                        daoHelper.fulfillAuditorFields(true, eea, usuarioDto.getId());
                        return eea;
                    }).collect(Collectors.toList());

            empresaEscrituraApoderadoRepository.saveAll(empresaEscrituraApoderados);
        }

        if(empresaEscrituraDto.getSocios() != null && empresaEscrituraDto.getSocios().size() > 0) {
            logger.info("Se encontraron socios. Agregando [{}] nuevos socios",
                    empresaEscrituraDto.getSocios().size());

            List<EmpresaEscrituraSocio> empresaEscrituraSocios = empresaEscrituraDto.getSocios()
                    .stream()
                    .map(so -> {
                        EmpresaEscrituraSocio ees = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraSocio(so);
                        ees.setEscritura(empresaEscrituraCreada.getId());
                        daoHelper.fulfillAuditorFields(true, ees, usuarioDto.getId());
                        return ees;
                    }).collect(Collectors.toList());

            empresaEscrituraSociosRepository.saveAll(empresaEscrituraSocios);
        }

        if(empresaEscrituraDto.getRepresentantes() != null && empresaEscrituraDto.getRepresentantes().size() > 0) {
            logger.info("Se encontraron representantes. Agregando [{}] nuevos representantes",
                    empresaEscrituraDto.getSocios().size());

            List<EmpresaEscrituraRepresentante> empresaEscrituraRepresentantes = empresaEscrituraDto.getRepresentantes()
                    .stream()
                    .map(re -> {
                        EmpresaEscrituraRepresentante eer = dtoToDaoConverter.convertDtoToDaoEmpresaRepresentante(re);
                        eer.setEscritura(empresaEscrituraCreada.getId());
                        daoHelper.fulfillAuditorFields(true, eer, usuarioDto.getId());
                        return eer;
                    }).collect(Collectors.toList());

            empresaEscrituraRepresentanteRepository.saveAll(empresaEscrituraRepresentantes);
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscrituraCreada);
    }
}
