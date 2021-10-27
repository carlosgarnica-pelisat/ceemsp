package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.EmpresaModalidad;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaModalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final UsuarioService usuarioService;
    private final EmpresaRepository empresaRepository;
    private final EmpresaModalidadRepository empresaModalidadRepository;
    private final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;

    @Autowired
    public EmpresaServiceImpl(UsuarioService usuarioService, EmpresaRepository empresaRepository, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaModalidadRepository empresaModalidadRepository) {
        this.usuarioService = usuarioService;
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaModalidadRepository = empresaModalidadRepository;
    }

    @Override
    public List<EmpresaDto> obtenerTodas() {
        List<Empresa> empresas = empresaRepository.getAllByEliminadoFalse();
        return empresas.stream().map(daoToDtoConverter::convertDaoToDtoEmpresa).collect(Collectors.toList());
    }

    @Override
    public EmpresaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la empresa a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresa);
    }

    @Override
    public EmpresaDto obtenerPorId(int id) {
        return null;
    }

    @Override
    @Transactional
    public EmpresaDto crearEmpresa(EmpresaDto empresaDto, String username) {
        if(StringUtils.isBlank(username) || empresaDto == null) {
            logger.warn("La empresa a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Dando de alta a una nueva empresa con razon social: [{}]", empresaDto.getNombreComercial());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        Empresa empresa = dtoToDaoConverter.convertDtoToDaoEmpresa(empresaDto);

        empresa.setFechaCreacion(LocalDateTime.now());
        empresa.setCreadoPor(usuario.getId());
        empresa.setActualizadoPor(usuario.getId());
        empresa.setFechaActualizacion(LocalDateTime.now());

        Empresa empresaCreada = empresaRepository.save(empresa);

        List<EmpresaModalidad> empresaModalidades = empresaDto.getModalidades().stream()
                .map(m -> {
                    EmpresaModalidad empresaModalidad = new EmpresaModalidad();
                    empresaModalidad.setUuid(RandomStringUtils.randomAlphanumeric(12));
                    empresaModalidad.setEmpresa(empresaCreada.getId());
                    empresaModalidad.setFechaCreacion(LocalDateTime.now());
                    empresaModalidad.setCreadoPor(usuario.getId());
                    empresaModalidad.setActualizadoPor(usuario.getId());
                    empresaModalidad.setFechaActualizacion(LocalDateTime.now());
                    empresaModalidad.setModalidad(m.getModalidad().getId());
                    if(m.getSubmodalidad() != null) {
                        empresaModalidad.setSubmodalidad(m.getSubmodalidad().getId());
                    }


                    return empresaModalidad;
                }).collect(Collectors.toList());

        List<EmpresaModalidad> createdModalidades = empresaModalidadRepository.saveAll(empresaModalidades);

        EmpresaDto response = daoToDtoConverter.convertDaoToDtoEmpresa(empresaCreada);
        response.setModalidades(createdModalidades.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaModalidad).collect(Collectors.toList()));

        return response;
    }

    @Override
    public EmpresaDto modificarEmpresa(EmpresaDto empresaDto, String username, String uuid) {
        return null;
    }

    @Override
    public EmpresaDto eliminarEmpresa(String username, String uuid) {
        return null;
    }
}
