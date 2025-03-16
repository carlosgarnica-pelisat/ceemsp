package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaVisitaServiceImpl implements EmpresaVisitaService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaVisitaServiceImpl.class);
    private final VisitaRepository visitaRepository;
    private final EmpresaService empresaService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final VisitaArchivoService visitaArchivoService;

    @Autowired
    public EmpresaVisitaServiceImpl(VisitaRepository visitaRepository, EmpresaService empresaService, DaoToDtoConverter daoToDtoConverter,
                                    VisitaArchivoService visitaArchivoService, UsuarioService usuarioService) {
        this.visitaRepository = visitaRepository;
        this.empresaService = empresaService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.visitaArchivoService = visitaArchivoService;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<VisitaDto> obtenerVisitasPorEmpresa(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros enviados no es valido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las visitas con el uuid [{}]", uuid);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        List<Visita> visitas = visitaRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());
        return visitas.stream().map(daoToDtoConverter::convertDaoToDtoVisita).collect(Collectors.toList());
    }

    @Override
    public VisitaDto obtenerVisitaPorUuid(String uuid, String visitaUuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros enviados no es valido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las visitas para la empresa con el uuid [{}]", uuid);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(visitaUuid);
        VisitaDto response = daoToDtoConverter.convertDaoToDtoVisita(visita);

        response.setResponsable(usuarioService.getUserById(visita.getResponsable()));
        response.setArchivos(visitaArchivoService.obtenerArchivosPorVisita(visitaUuid));

        return response;
    }

    @Override
    public File descargarArchivoVisita(String uuid, String visitaUuid, String archivoUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(visitaUuid) || StringUtils.isBlank(archivoUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando el archivo de la visita con uuid [{}]", archivoUuid);

        return visitaArchivoService.descargarArchivoVisita(visitaUuid, archivoUuid);
    }
}
