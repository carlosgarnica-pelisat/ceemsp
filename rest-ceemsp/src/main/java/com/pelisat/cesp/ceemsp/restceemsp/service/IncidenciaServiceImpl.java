package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenciaServiceImpl implements IncidenciaService {

    private static final int MAX_NUMEROS = 10;

    private final Logger logger = LoggerFactory.getLogger(IncidenciaService.class);
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final IncidenciaRepository incidenciaRepository;
    private final IncidenciaArmaRepository incidenciaArmaRepository;
    private final IncidenciaArchivoRepository incidenciaArchivoRepository;
    private final IncidenciaCanRepository incidenciaCanRepository;
    private final IncidenciaVehiculoRepository incidenciaVehiculoRepository;
    private final IncidenciaComentarioRepository incidenciaComentarioRepository;
    private final CanService canService;
    private final ArmaService armaService;
    private final EmpresaVehiculoService empresaVehiculoService;
    private final ClienteService clienteService;

    @Autowired
    public IncidenciaServiceImpl(UsuarioService usuarioService, EmpresaService empresaService, DaoToDtoConverter daoToDtoConverter,
                                 DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, IncidenciaRepository incidenciaRepository,
                                 IncidenciaArmaRepository incidenciaArmaRepository, IncidenciaArchivoRepository incidenciaArchivoRepository,
                                 IncidenciaCanRepository incidenciaCanRepository, IncidenciaVehiculoRepository incidenciaVehiculoRepository,
                                 IncidenciaComentarioRepository incidenciaComentarioRepository, CanService canService,
                                 ArmaService armaService, EmpresaVehiculoService empresaVehiculoService, ClienteService clienteService) {
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.incidenciaRepository = incidenciaRepository;
        this.incidenciaArmaRepository = incidenciaArmaRepository;
        this.incidenciaArchivoRepository = incidenciaArchivoRepository;
        this.incidenciaCanRepository = incidenciaCanRepository;
        this.incidenciaVehiculoRepository = incidenciaVehiculoRepository;
        this.incidenciaComentarioRepository = incidenciaComentarioRepository;
        this.canService = canService;
        this.armaService = armaService;
        this.empresaVehiculoService = empresaVehiculoService;
        this.clienteService = clienteService;
    }

    @Override
    public List<IncidenciaDto> obtenerIncidenciasPorEmpresa(String uuidEmpresa) {
        if(StringUtils.isBlank(uuidEmpresa)) {
            logger.warn("El uuid esta como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las incidencias con el uuid de la empresa [{}]", uuidEmpresa);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuidEmpresa);
        List<Incidencia> incidencias = incidenciaRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        List<IncidenciaDto> response = incidencias.stream().map(daoToDtoConverter::convertDaoToDtoIncidencia).collect(Collectors.toList());
        response.forEach(incidenciaDto -> {

        });

        return response;
    }

    @Override
    public IncidenciaDto obtenerIncidenciaPorUuid(String empresaUuid, String incidenciaUuid) {
        return null;
    }

    @Transactional
    @Override
    public IncidenciaDto guardarIncidencia(String empresaUuid, String username, IncidenciaDto incidenciaDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(username) || incidenciaDto == null) {
            logger.warn("El uuid, el usuario o la incidencia vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nueva incidendia");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        Incidencia incidencia = new Incidencia();
        incidencia.setNumero(RandomStringUtils.randomNumeric(MAX_NUMEROS));
        incidencia.setFechaIncidencia(LocalDate.parse(incidenciaDto.getFechaIncidencia()));
        incidencia.setStatus(IncidenciaStatusEnum.ABIERTA);

        if(incidenciaDto.isRelevancia()) {
            incidencia.setRelevancia(true);
            //TODO: agregar validacion para campos requeridos

            incidencia.setCliente(incidenciaDto.getCliente().getId());
            incidencia.setLatitud(incidenciaDto.getLatitud());
            incidencia.setLongitud(incidenciaDto.getLongitud());
            incidencia.setStatus(IncidenciaStatusEnum.ABIERTA);
            incidencia.setEmpresa(empresaDto.getId());
            daoHelper.fulfillAuditorFields(true, incidencia, usuarioDto.getId());
        } else {
            incidencia.setRelevancia(false);
        }

        Incidencia incidenciaCreada = incidenciaRepository.save(incidencia);

        if(incidenciaDto.getCanesInvolucrados() != null && incidenciaDto.getCanesInvolucrados().size() > 0) {
            logger.info("Hay canes involucrados");
            List<IncidenciaCan> canes = incidenciaDto.getCanesInvolucrados().stream().map(can -> {
                IncidenciaCan incidenciaCan = new IncidenciaCan();
                incidenciaCan.setIncidencia(incidenciaCreada.getId());
                incidenciaCan.setCan(canService.obtenerCanPorId(can.getId()).getId());
                daoHelper.fulfillAuditorFields(true, incidenciaCan, usuarioDto.getId());
                return incidenciaCan;
            }).collect(Collectors.toList());
            incidenciaCanRepository.saveAll(canes);
        }

        if(incidenciaDto.getArmasInvolucradas() != null && incidenciaDto.getArmasInvolucradas().size() > 0) {
            logger.info("Hay armas involucradas");
            List<IncidenciaArma> armas =  incidenciaDto.getArmasInvolucradas().stream().map(armaDto -> {
                IncidenciaArma incidenciaArma = new IncidenciaArma();
                incidenciaArma.setIncidencia(incidenciaCreada.getId());
                incidenciaArma.setArma(armaDto.getId());
                daoHelper.fulfillAuditorFields(true, incidenciaArma, usuarioDto.getId());
                return incidenciaArma;
            }).collect(Collectors.toList());
            incidenciaArmaRepository.saveAll(armas);
        }

        if(incidenciaDto.getVehiculosInvolucrados() != null && incidenciaDto.getVehiculosInvolucrados().size() > 0) {
            logger.info("Hay armas involucradas");
            List<IncidenciaVehiculo> vehiculos = incidenciaDto.getVehiculosInvolucrados().stream().map(vehiculoDto -> {
                IncidenciaVehiculo incidenciaVehiculo = new IncidenciaVehiculo();
                incidenciaVehiculo.setIncidencia(incidenciaCreada.getId());
                daoHelper.fulfillAuditorFields(true, incidenciaVehiculo, usuarioDto.getId());
                return incidenciaVehiculo;
            }).collect(Collectors.toList());
            incidenciaVehiculoRepository.saveAll(vehiculos);
        }

        if(incidenciaDto.getComentarios() != null && incidenciaDto.getComentarios().size() > 0) {
            logger.info("Hay comentarios");
            List<IncidenciaComentario> comentarios = incidenciaDto.getComentarios().stream().map(incidenciaComentario -> {
                IncidenciaComentario comentario = new IncidenciaComentario();
                comentario.setComentario(incidenciaComentario.getComentario());
                comentario.setIncidencia(incidenciaCreada.getId());
                daoHelper.fulfillAuditorFields(true, comentario, usuarioDto.getId());
                return comentario;
            }).collect(Collectors.toList());
            incidenciaComentarioRepository.saveAll(comentarios);
        }

        return daoToDtoConverter.convertDaoToDtoIncidencia(incidenciaCreada);
    }
}
