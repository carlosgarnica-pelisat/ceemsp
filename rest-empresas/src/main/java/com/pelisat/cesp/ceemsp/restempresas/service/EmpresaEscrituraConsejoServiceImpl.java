package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraConsejoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraConsejo;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraConsejoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
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
public class EmpresaEscrituraConsejoServiceImpl implements EmpresaEscrituraConsejoService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraConsejoService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final EmpresaEscrituraConsejoRepository empresaEscrituraConsejoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;

    @Autowired
    public EmpresaEscrituraConsejoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                              DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                              EmpresaEscrituraConsejoRepository empresaEscrituraConsejoRepository,
                                              EmpresaEscrituraRepository empresaEscrituraRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaEscrituraConsejoRepository = empresaEscrituraConsejoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
    }
    @Override
    public List<EmpresaEscrituraConsejoDto> obtenerConsejosPorEscritura(String escrituraUuid) {
        if(StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaEscrituraConsejo> empresaEscrituraConsejos = empresaEscrituraConsejoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

        return empresaEscrituraConsejos.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraConsejo)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraConsejoDto crearConsejo(String escrituraUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto) {
        if(empresaEscrituraConsejoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El consejo, la empresa o la escritura estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando consejo en la escritura: [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraConsejo empresaEscrituraConsejo = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraConsejo(empresaEscrituraConsejoDto);
        empresaEscrituraConsejo.setEscritura(empresaEscritura.getId());
        daoHelper.fulfillAuditorFields(true, empresaEscrituraConsejo, usuario.getId());
        EmpresaEscrituraConsejo empresaEscrituraConsejoCreado = empresaEscrituraConsejoRepository.save(empresaEscrituraConsejo);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraConsejo(empresaEscrituraConsejoCreado);
    }

    @Override
    public EmpresaEscrituraConsejoDto actualizarConsejo(String escrituraUuid, String consejoUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto) {
        if(StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(consejoUuid) || empresaEscrituraConsejoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta modificando el consejo de la escritura con el uuid [{}]", consejoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraConsejo empresaEscrituraConsejo = empresaEscrituraConsejoRepository.findByUuidAndEliminadoFalse(consejoUuid);

        if(empresaEscrituraConsejo == null) {
            logger.warn("El consejo a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraConsejo.setNombres(empresaEscrituraConsejoDto.getNombres());
        empresaEscrituraConsejo.setApellidos(empresaEscrituraConsejoDto.getApellidos());
        empresaEscrituraConsejo.setApellidoMaterno(empresaEscrituraConsejoDto.getApellidoMaterno());
        empresaEscrituraConsejo.setCurp(empresaEscrituraConsejoDto.getCurp());
        empresaEscrituraConsejo.setSexo(empresaEscrituraConsejoDto.getSexo());
        empresaEscrituraConsejo.setPuesto(empresaEscrituraConsejoDto.getPuesto());
        daoHelper.fulfillAuditorFields(false, empresaEscrituraConsejo, usuario.getId());
        empresaEscrituraConsejoRepository.save(empresaEscrituraConsejo);

        return empresaEscrituraConsejoDto;
    }

    @Override
    public EmpresaEscrituraConsejoDto eliminarConsejo(String escrituraUuid, String consejoUuid, String username) {
        if(StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(consejoUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el consejo con el ID [{}]", consejoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraConsejo empresaEscrituraConsejo = empresaEscrituraConsejoRepository.findByUuidAndEliminadoFalse(consejoUuid);

        if(empresaEscrituraConsejo == null) {
            logger.warn("El consejo a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraConsejo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaEscrituraConsejo, usuario.getId());
        empresaEscrituraConsejoRepository.save(empresaEscrituraConsejo);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraConsejo(empresaEscrituraConsejo);
    }
}
