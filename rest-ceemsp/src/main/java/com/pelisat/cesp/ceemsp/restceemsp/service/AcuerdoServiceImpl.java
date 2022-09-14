package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.AcuerdoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Acuerdo;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.AcuerdoRepository;
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

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcuerdoServiceImpl implements AcuerdoService {
    private final AcuerdoRepository acuerdoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final ArchivosService archivosService;
    private Logger logger = LoggerFactory.getLogger(AcuerdoService.class);

    @Autowired
    public AcuerdoServiceImpl(AcuerdoRepository acuerdoRepository, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                              UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper, EmpresaService empresaService,
                              ArchivosService archivosService) {
        this.acuerdoRepository = acuerdoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.archivosService = archivosService;
    }

    @Override
    public List<AcuerdoDto> obtenerAcuerdosEmpresa(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los acuerdos de la empresa [{}]", uuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        List<Acuerdo> acuerdos = acuerdoRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return acuerdos.stream().map(daoToDtoConverter::convertDaoToDtoAcuerdo).collect(Collectors.toList());
    }

    @Override
    public AcuerdoDto obtenerAcuerdoPorUuid(String uuid, String acuerdoUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(acuerdoUuid)) {
            logger.warn("Alguno de los parametros viene como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el acuerdo con el uuid [{}]", acuerdoUuid);

        Acuerdo acuerdo = acuerdoRepository.getByUuid(acuerdoUuid);
        if(acuerdo == null) {
            logger.warn("El acuerdo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoAcuerdo(acuerdo);
    }

    @Override
    public File obtenerArchivoAcuerdo(String uuid, String acuerdoUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(acuerdoUuid)) {
            logger.warn("Alguno de los parametros viene como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el acuerdo con el uuid [{}]", acuerdoUuid);

        Acuerdo acuerdo = acuerdoRepository.getByUuid(acuerdoUuid);
        if(acuerdo == null) {
            logger.warn("El acuerdo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(acuerdo.getRutaArchivo());
    }

    @Override
    public AcuerdoDto guardarAcuerdo(String uuid, AcuerdoDto acuerdoDto, String username, MultipartFile multipartFile) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || acuerdoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Guardando el acuerdo para la empresa [{}]", uuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        Acuerdo acuerdo = dtoToDaoConverter.convertDtoToDaoAcuerdo(acuerdoDto);
        daoHelper.fulfillAuditorFields(true, acuerdo, usuarioDto.getId());
        acuerdo.setFecha(LocalDate.parse(acuerdoDto.getFecha()));
        acuerdo.setEmpresa(empresaDto.getId());

        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.EMPRESA_ACUERDO, uuid);
            acuerdo.setRutaArchivo(ruta);
            Acuerdo acuerdoCreado = acuerdoRepository.save(acuerdo);
            return daoToDtoConverter.convertDaoToDtoAcuerdo(acuerdoCreado);
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    public AcuerdoDto modificarAcuerdo(String uuid, String acuerdoUuid, AcuerdoDto acuerdoDto, String username, MultipartFile multipartFile) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(acuerdoUuid) || acuerdoDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Modificando el acuerdo con el uuid [{}]", acuerdoUuid);

        Acuerdo acuerdo = acuerdoRepository.getByUuid(acuerdoUuid);
        if(acuerdo == null) {
            logger.warn("El acuerdo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        daoHelper.fulfillAuditorFields(false, acuerdo, usuarioDto.getId());

        acuerdo.setFecha(LocalDate.parse(acuerdoDto.getFecha()));
        acuerdo.setObservaciones(acuerdoDto.getObservaciones());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Eliminando y modificando");
            archivosService.eliminarArchivo(acuerdo.getRutaArchivo());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.EMPRESA_ACUERDO, uuid);
                acuerdo.setRutaArchivo(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        Acuerdo acuerdoEliminado = acuerdoRepository.save(acuerdo);
        return daoToDtoConverter.convertDaoToDtoAcuerdo(acuerdoEliminado);
    }

    @Override
    public AcuerdoDto eliminarAcuerdo(String uuid, String acuerdoUuid, String username, AcuerdoDto acuerdoDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(acuerdoUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el acuerdo con el uuid [{}]", acuerdoUuid);

        Acuerdo acuerdo = acuerdoRepository.getByUuid(acuerdoUuid);
        if(acuerdo == null) {
            logger.warn("El acuerdo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        acuerdo.setMotivoBaja(acuerdoDto.getMotivoBaja());
        acuerdo.setObservacionesBaja(acuerdoDto.getObservacionesBaja());
        acuerdo.setFechaBaja(LocalDate.parse(acuerdoDto.getFechaBaja()));
        acuerdo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, acuerdo, usuarioDto.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_ACUERDO, acuerdoUuid);
                acuerdo.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        Acuerdo acuerdoEliminado = acuerdoRepository.save(acuerdo);

        return daoToDtoConverter.convertDaoToDtoAcuerdo(acuerdoEliminado);
    }
}
