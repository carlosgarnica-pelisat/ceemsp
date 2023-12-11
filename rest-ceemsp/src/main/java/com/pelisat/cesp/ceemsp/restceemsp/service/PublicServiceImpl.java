package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

@Service
public class PublicServiceImpl implements PublicService {

    private final EmpresaRepository empresaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final VisitaRepository visitaRepository;
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(PublicService.class);
    private final AcuseRepository acuseRepository;
    private final NotificacionRepository notificacionRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final EmpresaReporteMensualRepository empresaReporteMensualRepository;

    @Autowired
    public PublicServiceImpl(EmpresaRepository empresaRepository, VehiculoRepository vehiculoRepository,
                             DaoToDtoConverter daoToDtoConverter, VisitaRepository visitaRepository,
                             UsuarioRepository usuarioRepository, AcuseRepository acuseRepository,
                             NotificacionRepository notificacionRepository, EmpresaReporteMensualRepository empresaReporteMensualRepository,
                             IncidenciaRepository incidenciaRepository) {
        this.empresaRepository = empresaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.visitaRepository = visitaRepository;
        this.usuarioRepository = usuarioRepository;
        this.acuseRepository = acuseRepository;
        this.notificacionRepository = notificacionRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.empresaReporteMensualRepository = empresaReporteMensualRepository;
    }

    @Override
    public NextRegisterDto findNextRegister(NextRegisterDto nextRegisterDto) {
        if(nextRegisterDto == null) {
            logger.warn("El dto para consultar el proximo registro viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.findFirstByTipoTramiteOrderByFechaCreacionDesc(nextRegisterDto.getTipo());
        NextRegisterDto response = new NextRegisterDto();

        if(empresa == null) {
            response.setNumeroSiguiente("001");
        } else {
            String currentNumber;
            if(nextRegisterDto.getTipo() == TipoTramiteEnum.AP) {
                currentNumber = empresa.getRegistro().split("/")[3];
            } else {
                currentNumber = empresa.getRegistro().split("/")[2];
            }

            int nextNumber = Integer.parseInt(currentNumber);
            nextNumber = nextNumber+1;
            String nextNumberString = Integer.toString(nextNumber);
            while(nextNumberString.length() < 3) {
                nextNumberString = "0" + nextNumberString;
            }
            response.setNumeroSiguiente(nextNumberString);
        }

        return response;
    }

    @Override
    public ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto) {
        if(existeVehiculoDto == null) {
            logger.warn("El objeto para buscar la existencia del vehiculo no existe.");
            throw new InvalidDataException();
        }

        logger.info("Consultando vehiculo con placa [{}] y serie [{}]", existeVehiculoDto.getPlacas(), existeVehiculoDto.getNumeroSerie());
        ExisteVehiculoDto response = new ExisteVehiculoDto();

        Vehiculo vehiculoPorSerie = vehiculoRepository.getBySerieAndEliminadoFalse(existeVehiculoDto.getNumeroSerie());

        if(vehiculoPorSerie != null) {
            logger.info("Se encontro vehiculo por numero de serie");
            response.setExiste(true);
            response.setVehiculo(daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoPorSerie));
            response.setNumeroSerie(existeVehiculoDto.getNumeroSerie());
            return response;
        }

        Vehiculo vehiculoPorPlacas = vehiculoRepository.getByPlacasAndEliminadoFalse(existeVehiculoDto.getPlacas());

        if(vehiculoPorPlacas != null) {
            logger.info("Se encontro vehiculo por placas");
            response.setExiste(true);
            response.setVehiculo(daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoPorPlacas));
            response.setPlacas(existeVehiculoDto.getPlacas());
            return response;
        }

        response.setExiste(false);

        return response;
    }

    @Override
    public ProximaVisitaDto buscarProximaVisita(ProximaVisitaDto proximaVisitaDto) {
        if(proximaVisitaDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Visita visita = visitaRepository.findFirstByTipoVisitaAndEliminadoFalseOrderByFechaCreacionDesc(proximaVisitaDto.getTipoVisita());
        ProximaVisitaDto response = new ProximaVisitaDto();

        if(visita == null) {
            response.setNumeroSiguiente("001");
        } else {
            String currentNumber = visita.getNumeroOrden().split("/")[3];
            int nextNumber = Integer.parseInt(currentNumber);
            nextNumber = nextNumber+1;
            String nextNumberString = Integer.toString(nextNumber);
            while(nextNumberString.length() < 3) {
                nextNumberString = "0" + nextNumberString;
            }
            response.setNumeroSiguiente(nextNumberString);
        }

        return response;
    }

    @Override
    public String buscarProximoNumeroAcuse() {
        Acuse acuse = acuseRepository.findFirstByEliminadoFalseOrderByFechaCreacionDesc();
        if(Objects.isNull(acuse)) {
            return "0001";
        } else {
            String currentNumber = acuse.getNumero().split("/")[3];
            int nextNumber = Integer.parseInt(currentNumber);
            nextNumber = nextNumber + 1;
            String nextNumberString = Integer.toString(nextNumber);
            while(nextNumberString.length() < 4) {
                nextNumberString = "0" + nextNumberString;
            }
            return nextNumberString;
        }
    }

    @Override
    public String buscarProximoNumeroReporte() {
        EmpresaReporteMensual reporte = empresaReporteMensualRepository.findFirstByEliminadoFalseOrderByFechaCreacionDesc();
        if(Objects.isNull(reporte)) {
            return "0001";
        } else {
            String currentNumber = reporte.getNumero().split("/")[3];
            int nextNumber = Integer.parseInt(currentNumber);
            nextNumber = nextNumber + 1;
            String nextNumberString = Integer.toString(nextNumber);
            while(nextNumberString.length() < 4) {
                nextNumberString = "0" + nextNumberString;
            }
            return nextNumberString;
        }
    }

    @Override
    public String buscarProximoNumeroNotificacion() {
        Notificacion notificacion = notificacionRepository.findFirstByEliminadoFalseOrderByFechaCreacionDesc();
        if(Objects.isNull(notificacion)) {
            return "0001";
        } else {
            String currentNumber = notificacion.getNumero().split("/")[3];
            int nextNumber = Integer.parseInt(currentNumber);
            nextNumber = nextNumber + 1;
            String nextNumberString = Integer.toString(nextNumber);
            while(nextNumberString.length() < 4) {
                nextNumberString = "0" + nextNumberString;
            }
            return nextNumberString;
        }
    }

    @Override
    public ValidarAcuseDto validarAcusePorSello(String sello) {
        if(StringUtils.isBlank(sello)) {
            logger.warn("Alguno de los parametros es invalido");
            throw new InvalidDataException();
        }

        Acuse acuse = acuseRepository.findBySelloAndEliminadoFalse(sello);
        if(acuse == null) {
            logger.warn("No existe el acuse con el sello dado");
            throw new NotFoundResourceException();
        }

        Incidencia incidencia = incidenciaRepository.getOne(acuse.getIncidencia());

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Empresa empresa = empresaRepository.getOne(incidencia.getEmpresa());

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        ValidarAcuseDto validarAcuseDto = new ValidarAcuseDto();
        validarAcuseDto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresa));
        validarAcuseDto.setIncidencia(daoToDtoConverter.convertDaoToDtoIncidencia(incidencia));
        validarAcuseDto.setNumeroAcuse(acuse.getNumero());
        return validarAcuseDto;
    }

    @Override
    public ValidarInformeDto validarInformePorSello(String sello) {
        if(StringUtils.isBlank(sello)) {
            logger.warn("Alguno de los parametros es invalido");
            throw new InvalidDataException();
        }

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.findBySelloAndEliminadoFalse(sello);
        if(reporteMensual == null) {
            logger.warn("No existe el acuse con el sello dado");
            throw new NotFoundResourceException();
        }

        Empresa empresa = empresaRepository.getOne(reporteMensual.getEmpresa());

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        ValidarInformeDto validarInformeDto = new ValidarInformeDto();
        validarInformeDto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresa));
        validarInformeDto.setFechaCreacion(reporteMensual.getFechaCreacion().toString());
        validarInformeDto.setNumero(reporteMensual.getNumero());
        LocalDateTime reporteDate = reporteMensual.getFechaCreacion().minusMonths(1);
        Month mes = reporteDate.getMonth();
        validarInformeDto.setMesano(mes.getDisplayName(TextStyle.FULL, new Locale("es", "MX")).toUpperCase() + " " + reporteDate.getYear());
        return validarInformeDto;
    }
}
