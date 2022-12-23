package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraApoderadoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepresentanteRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaEscrituraApoderadoServiceImpl implements EmpresaEscrituraApoderadoService {

    private final EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final ArchivosService archivosService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraSocioService.class);

    @Autowired
    public EmpresaEscrituraApoderadoServiceImpl(EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository, EmpresaEscrituraRepository empresaEscrituraRepository,
                                                    UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                                    DaoHelper<CommonModel> daoHelper, EmpresaService empresaService, ArchivosService archivosService) {
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.archivosService = archivosService;
    }

    @Override
    public List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(String empresaUuid, String escrituraUuid) {
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

        List<EmpresaEscrituraApoderado> empresaEscrituraApoderados = empresaEscrituraApoderadoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

        return empresaEscrituraApoderados.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraApoderado)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaEscrituraApoderadoDto> obtenerTodosApoderadosPorEscritura(String empresaUuid, String escrituraUuid) {
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

        List<EmpresaEscrituraApoderado> empresaEscrituraApoderados = empresaEscrituraApoderadoRepository.findAllByEscritura(empresaEscritura.getId());

        return empresaEscrituraApoderados.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraApoderado)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraApoderadoDto obtenerRepresentantePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad) {
        return null;
    }

    @Override
    @Transactional
    public EmpresaEscrituraApoderadoDto crearApoderado(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto) {
        if(empresaEscrituraApoderadoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El apoderado, la empresa o la escritura estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando apoderado en la escritura: [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraApoderado empresaEscrituraApoderado = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraApoderado(empresaEscrituraApoderadoDto);
        empresaEscrituraApoderado.setEscritura(empresaEscritura.getId());
        if(StringUtils.isNotBlank(empresaEscrituraApoderadoDto.getFechaInicio())) {
            empresaEscrituraApoderado.setFechaInicio(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaInicio()));
        }

        if(StringUtils.isNotBlank(empresaEscrituraApoderadoDto.getFechaFin())) {
            empresaEscrituraApoderado.setFechaFin(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaFin()));
        }

        daoHelper.fulfillAuditorFields(true, empresaEscrituraApoderado, usuario.getId());
        EmpresaEscrituraApoderado empresaEscrituraRepresentanteCreada = empresaEscrituraApoderadoRepository.save(empresaEscrituraApoderado);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(empresaEscrituraRepresentanteCreada);
    }

    @Override
    @Transactional
    public EmpresaEscrituraApoderadoDto modificarApoderado(String empresaUuid, String escrituraUuid, String apoderadoUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(apoderadoUuid) || empresaEscrituraApoderadoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta modificando el apoderado de la escritura con el uuid [{}]", apoderadoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraApoderado empresaEscrituraApoderado = empresaEscrituraApoderadoRepository.findByUuidAndEliminadoFalse(apoderadoUuid);

        if(empresaEscrituraApoderado == null) {
            logger.warn("El apoderado a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraApoderado.setNombres(empresaEscrituraApoderadoDto.getNombres());
        empresaEscrituraApoderado.setApellidos(empresaEscrituraApoderadoDto.getApellidos());
        empresaEscrituraApoderado.setApellidoMaterno(empresaEscrituraApoderadoDto.getApellidoMaterno());
        empresaEscrituraApoderado.setCurp(empresaEscrituraApoderadoDto.getCurp());
        if(StringUtils.isNotBlank(empresaEscrituraApoderadoDto.getFechaInicio())) {
            empresaEscrituraApoderado.setFechaInicio(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaInicio()));
        }

        if(StringUtils.isNotBlank(empresaEscrituraApoderadoDto.getFechaFin())) {
            empresaEscrituraApoderado.setFechaFin(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaFin()));
        }

        daoHelper.fulfillAuditorFields(false, empresaEscrituraApoderado, usuario.getId());
        empresaEscrituraApoderadoRepository.save(empresaEscrituraApoderado);
        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(empresaEscrituraApoderado);
    }

    @Override
    public EmpresaEscrituraApoderadoDto eliminarApoderado(String empresaUuid, String escrituraUuid, String apoderadoUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(apoderadoUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta eliminando el apoderado de la escritura con el uuid [{}]", apoderadoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraApoderado empresaEscrituraApoderado = empresaEscrituraApoderadoRepository.findByUuidAndEliminadoFalse(apoderadoUuid);

        if(empresaEscrituraApoderado == null) {
            logger.warn("El apoderado a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraApoderado.setObservacionesBaja(empresaEscrituraApoderadoDto.getObservacionesBaja());
        empresaEscrituraApoderado.setFechaBaja(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaBaja()));
        empresaEscrituraApoderado.setMotivoBaja(empresaEscrituraApoderadoDto.getMotivoBaja());
        empresaEscrituraApoderado.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaEscrituraApoderado, usuario.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_APODERADO, empresaUuid);
                empresaEscrituraApoderado.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        empresaEscrituraApoderadoRepository.save(empresaEscrituraApoderado);
        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(empresaEscrituraApoderado);
    }

    @Override
    public File obtenerDocumentoFundatorioBajaApoderado(String empresaUuid, String escrituraUuid, String apoderadoUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(apoderadoUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando el documento fundatorio con uuid [{}]", apoderadoUuid);
        EmpresaEscrituraApoderado apoderado = empresaEscrituraApoderadoRepository.findByUuid(apoderadoUuid);

        if(apoderado == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(!apoderado.getEliminado()) {
            logger.warn("El elemento no se encuentra eliminado");
            throw new NotFoundResourceException();
        }

        return new File(apoderado.getDocumentoFundatorioBaja());
    }
}
