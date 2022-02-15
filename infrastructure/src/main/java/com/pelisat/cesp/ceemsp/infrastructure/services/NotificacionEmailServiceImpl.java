package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class NotificacionEmailServiceImpl implements NotificacionEmailService {

    @Override
    public void enviarEmail(NotificacionEmailEnum tipoEmail, EmpresaDto empresaDto) throws MessagingException {

    }
}
