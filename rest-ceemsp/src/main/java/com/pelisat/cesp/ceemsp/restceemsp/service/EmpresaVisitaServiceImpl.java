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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaVisitaServiceImpl implements EmpresaVisitaService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaVisitaServiceImpl.class);
    private final VisitaRepository visitaRepository;
    private final EmpresaService empresaService;
    private final DaoToDtoConverter daoToDtoConverter;

    @Autowired
    public EmpresaVisitaServiceImpl(VisitaRepository visitaRepository, EmpresaService empresaService, DaoToDtoConverter daoToDtoConverter) {
        this.visitaRepository = visitaRepository;
        this.empresaService = empresaService;
        this.daoToDtoConverter = daoToDtoConverter;
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
        List<VisitaDto> response = visitas.stream().map(daoToDtoConverter::convertDaoToDtoVisita).collect(Collectors.toList());
        return response;
    }
}
