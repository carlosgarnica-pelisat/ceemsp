package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionCorreoDto;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.restceemsp.task.ProximosVencimientosTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionesCorreoServiceImpl implements NotificacionesCorreoService {

    private final Logger logger = LoggerFactory.getLogger(NotificacionesCorreoService.class);
    private final ProximosVencimientosTask proximosVencimientosTask;

    @Autowired
    public NotificacionesCorreoServiceImpl(ProximosVencimientosTask proximosVencimientosTask) {
        this.proximosVencimientosTask = proximosVencimientosTask;
    }

    @Override
    public void generarCorreoElectronico(String username, NotificacionCorreoDto correoDto) {
        if(StringUtils.isBlank(username) || correoDto == null) {
            logger.warn("Alguno de los parametros son nulos o invalidos");
            throw new InvalidDataException();
        }

        proximosVencimientosTask.enviarNotificacion(correoDto.getNotificacion());
    }
}
