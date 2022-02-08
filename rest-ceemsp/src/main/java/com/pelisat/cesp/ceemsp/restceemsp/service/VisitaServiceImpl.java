package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoUsoDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
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
public class VisitaServiceImpl implements VisitaService {

    private final Logger logger = LoggerFactory.getLogger(VisitaService.class);
    private final VisitaRepository visitaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public VisitaServiceImpl(VisitaRepository visitaRepository, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                             EmpresaService empresaService, UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper) {
        this.visitaRepository = visitaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<VisitaDto> obtenerTodas() {
        logger.info("Consultando todas las visitas en la base de datos");
        List<Visita> visitas = visitaRepository.getAllByEliminadoFalse();
        return visitas.stream()
                .map(v -> {
                    VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(v);
                    visitaDto.setEmpresa(empresaService.obtenerPorId(v.getEmpresa()));
                    visitaDto.setResponsable(usuarioService.getUserById(v.getResponsable()));
                    return visitaDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public VisitaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la visita a consultar viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);
        VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(visita);

        visitaDto.setEmpresa(empresaService.obtenerPorId(visita.getEmpresa()));
        visitaDto.setResponsable(usuarioService.getUserById(visita.getResponsable()));

        return visitaDto;
    }

    @Override
    public VisitaDto obtenerPorId(Integer id) {
        return null;
    }

    @Override
    public VisitaDto crearNuevo(VisitaDto visitaDto, String username) {
        if(StringUtils.isBlank(username) || visitaDto == null) {
            logger.warn("El usuario o la visita a registrar viene como nula o vacia");
            throw new InvalidDataException();
        }

        logger.info("Registrando una nueva visita");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Visita visita = dtoToDaoConverter.convertDtoToDaoVisita(visitaDto);
        visita.setEmpresa(visitaDto.getEmpresa().getId());
        visita.setResponsable(visitaDto.getResponsable().getId());
        daoHelper.fulfillAuditorFields(true, visita, usuarioDto.getId());

        Visita visitaCreada = visitaRepository.save(visita);

        return daoToDtoConverter.convertDaoToDtoVisita(visitaCreada);
    }
}
