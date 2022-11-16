package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {
    void sendEmail(NotificacionEmailEnum notificacionEmailEnum, String to) throws MessagingException, TemplateException, IOException;
}
