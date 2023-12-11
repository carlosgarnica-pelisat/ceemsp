package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.AcuerdoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Acuerdo;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.AcuerdoRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
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
public class EmpresaAcuerdoServiceImpl implements EmpresaAcuerdoService {

    private final AcuerdoRepository acuerdoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaAcuerdoService.class);

    @Autowired
    public EmpresaAcuerdoServiceImpl(AcuerdoRepository acuerdoRepository, DaoToDtoConverter daoToDtoConverter,
                                     UsuarioService usuarioService) {
        this.acuerdoRepository = acuerdoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<AcuerdoDto> obtenerAcuerdosEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.debug("Obteniendo los acuerdos de la empresa [{}]", username);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Acuerdo> acuerdos = acuerdoRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return acuerdos.stream().map(daoToDtoConverter::convertDaoToDtoAcuerdo).collect(Collectors.toList());
    }

    @Override
    public AcuerdoDto obtenerAcuerdoPorUuid(String acuerdoUuid) {
        if(StringUtils.isBlank(acuerdoUuid)) {
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
    public File obtenerArchivoAcuerdo(String acuerdoUuid) {
        if(StringUtils.isBlank(acuerdoUuid)) {
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
}
