package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraApoderado;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraApoderadoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ArchivosService archivosService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraSocioService.class);

    @Autowired
    public EmpresaEscrituraApoderadoServiceImpl(EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository, EmpresaEscrituraRepository empresaEscrituraRepository,
                                                UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                                DaoHelper<CommonModel> daoHelper, ArchivosService archivosService) {
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.archivosService = archivosService;
    }

    @Override
    public List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(String escrituraUuid) {
        if(StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }
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
    @Transactional
    public EmpresaEscrituraApoderadoDto crearApoderado(String escrituraUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto) {
        if(empresaEscrituraApoderadoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(escrituraUuid)) {
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
        empresaEscrituraApoderado.setFechaInicio(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaInicio()));
        empresaEscrituraApoderado.setFechaFin(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaFin()));
        daoHelper.fulfillAuditorFields(true, empresaEscrituraApoderado, usuario.getId());
        EmpresaEscrituraApoderado empresaEscrituraRepresentanteCreada = empresaEscrituraApoderadoRepository.save(empresaEscrituraApoderado);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(empresaEscrituraRepresentanteCreada);
    }

    @Override
    @Transactional
    public EmpresaEscrituraApoderadoDto modificarApoderado(String escrituraUuid, String apoderadoUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto) {
        if(StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(apoderadoUuid) || empresaEscrituraApoderadoDto == null) {
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
        empresaEscrituraApoderado.setFechaInicio(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaInicio()));
        empresaEscrituraApoderado.setFechaFin(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaFin()));

        daoHelper.fulfillAuditorFields(false, empresaEscrituraApoderado, usuario.getId());
        empresaEscrituraApoderadoRepository.save(empresaEscrituraApoderado);
        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(empresaEscrituraApoderado);
    }

    @Transactional
    @Override
    public EmpresaEscrituraApoderadoDto eliminarApoderado(String escrituraUuid, String apoderadoUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(apoderadoUuid)) {
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
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_APODERADO, usuario.getEmpresa().getUuid());
                empresaEscrituraApoderado.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        empresaEscrituraApoderadoRepository.save(empresaEscrituraApoderado);
        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(empresaEscrituraApoderado);
    }
}
