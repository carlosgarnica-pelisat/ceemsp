package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;

import javax.mail.MessagingException;

public interface NotificacionEmailService {
    //void enviarEmail(NotificacionEmailEnum tipoEmail, EmpresaDto empresaDto, UsuarioDto usuarioEmisor, UsuarioDto usuarioReceptor) throws MessagingException;

    // TODO: Verificar si esto se va a realizar o nope.
    //void sendRecoveryPasswordEmail(RecoverPasswordToken recoverPasswordToken, String email) throws MessagingException;
}
