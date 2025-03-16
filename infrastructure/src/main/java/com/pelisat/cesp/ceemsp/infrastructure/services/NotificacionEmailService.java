package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface NotificacionEmailService {
    void enviarEmail(NotificacionEmailEnum tipoEmail, EmpresaDto empresaDto, UsuarioDto usuarioEmisor, UsuarioDto usuarioReceptor) throws MessagingException, TemplateException, IOException;
    void enviarEmailNotificacion(NotificacionEmailEnum tipoEmail);

    // TODO: Verificar si esto se va a realizar o nope.
    //void sendRecoveryPasswordEmail(RecoverPasswordToken recoverPasswordToken, String email) throws MessagingException;
}
