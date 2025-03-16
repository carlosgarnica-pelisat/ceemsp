package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.Acuse;
import com.pelisat.cesp.ceemsp.database.model.EmpresaReporteMensual;
import com.pelisat.cesp.ceemsp.database.model.Notificacion;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PublicServiceImpl implements PublicService {
    private final VehiculoRepository vehiculoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(PublicService.class);
    private final AcuseRepository acuseRepository;
    private final NotificacionRepository notificacionRepository;
    private final EmpresaReporteMensualRepository empresaReporteMensualRepository;

    @Autowired
    public PublicServiceImpl(VehiculoRepository vehiculoRepository, NotificacionRepository notificacionRepository,
                             DaoToDtoConverter daoToDtoConverter, AcuseRepository acuseRepository, EmpresaReporteMensualRepository empresaReporteMensualRepository
                             ) {
        this.vehiculoRepository = vehiculoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.acuseRepository = acuseRepository;
        this.notificacionRepository = notificacionRepository;
        this.empresaReporteMensualRepository = empresaReporteMensualRepository;
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
            String currentNumber = reporte.getNumero().split("/")[4];
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
}
