package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.metadata.VehiculoFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.model.VehiculoFotografia;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoFotografiaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaVehiculoFotografiaServiceImpl implements EmpresaVehiculoFotografiaService {
    private final Logger logger = LoggerFactory.getLogger(EmpresaVehiculoFotografiaService.class);
    private final VehiculoFotografiaRepository vehiculoFotografiaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final ArchivosService archivosService;
    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public EmpresaVehiculoFotografiaServiceImpl(VehiculoFotografiaRepository vehiculoFotografiaRepository, DaoToDtoConverter daoToDtoConverter,
                                         DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, EmpresaService empresaService,
                                         UsuarioService usuarioService, ArchivosService archivosService, VehiculoRepository vehiculoRepository) {
        this.vehiculoFotografiaRepository = vehiculoFotografiaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.archivosService = archivosService;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public List<VehiculoFotografiaMetadata> mostrarVehiculoFotografias(String vehiculoUuid) {
        if(StringUtils.isBlank(vehiculoUuid)) {
            logger.warn("El uuid de la empresa o el vehiculo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el metadata de las fotos del vehiculo [{}]", vehiculoUuid);

        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);
        if(vehiculo == null) {
            logger.warn("el vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<VehiculoFotografia> metadata = vehiculoFotografiaRepository.getAllByVehiculoAndEliminadoFalse(vehiculo.getId());

        return metadata.stream().map(m -> {
            VehiculoFotografiaMetadata vfm = new VehiculoFotografiaMetadata();
            String[] tokens = m.getUbicacionArchivo().split("[\\\\|/]");
            vfm.setId(m.getId());
            vfm.setDescripcion(m.getDescripcion());
            vfm.setUuid(m.getUuid());
            vfm.setNombreArchivo(tokens[tokens.length - 1]);
            return vfm;
        }).collect(Collectors.toList());
    }

    @Override
    public File descargarFotografiaVehiculo(String vehiculoUuid, String fotografiaUuid) {
        if(StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(fotografiaUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando la fotografia del vehiculo con uuid [{}]", fotografiaUuid);
        VehiculoFotografia vehiculoFotografia = vehiculoFotografiaRepository.getByUuidAndEliminadoFalse(fotografiaUuid);

        if(vehiculoFotografia == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(vehiculoFotografia.getUbicacionArchivo());
    }

    @Override
    @Transactional
    public void guardarVehiculoFotografia(String vehiculoUuid, String username, MultipartFile multipartFile, VehiculoFotografiaMetadata metadata) {
        if (StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username) || multipartFile == null) {
            logger.warn("El uuid de la empresa o la persona o la foto vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando el nuevo vehiculo en la base de datos");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoUuid);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        VehiculoFotografia vehiculoFotografia = new VehiculoFotografia();
        vehiculoFotografia.setVehiculo(vehiculo.getId());
        vehiculoFotografia.setDescripcion(metadata.getDescripcion());
        daoHelper.fulfillAuditorFields(true, vehiculoFotografia, usuarioDto.getId());

        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.FOTOGRAFIA_VEHICULO, usuarioDto.getEmpresa().getUuid());
            vehiculoFotografia.setUbicacionArchivo(ruta);
            vehiculoFotografiaRepository.save(vehiculoFotografia);

            if(!vehiculo.isFotografiaCapturada()) {
                vehiculo.setFotografiaCapturada(true);
                daoHelper.fulfillAuditorFields(false, vehiculo, usuarioDto.getId());
                vehiculoRepository.save(vehiculo);
            }
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    @Transactional
    public void eliminarVehiculoFotografia(String vehiculoUuid, String fotografiaUuid, String username) {
        if(StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(fotografiaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la fotografia con uuid [{}]", fotografiaUuid);
        VehiculoFotografia vehiculoFotografia = vehiculoFotografiaRepository.getByUuidAndEliminadoFalse(fotografiaUuid);

        if(vehiculoFotografia == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        archivosService.eliminarArchivo(vehiculoFotografia.getUbicacionArchivo());
        vehiculoFotografia.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, vehiculoFotografia, usuarioDto.getId());
        vehiculoFotografiaRepository.save(vehiculoFotografia);
    }
}
