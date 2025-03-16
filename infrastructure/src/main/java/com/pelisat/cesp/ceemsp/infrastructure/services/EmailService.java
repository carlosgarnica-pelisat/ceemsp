package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import com.pelisat.cesp.ceemsp.database.type.NotificacionInternalEmailEnum;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface EmailService {
    void sendEmail(NotificacionEmailEnum notificacionEmailEnum, String to, Map map) throws MessagingException, TemplateException, IOException;
    void sendInternalMail(NotificacionInternalEmailEnum notificacionInternalEmailEnum, String to, List<Map> map) throws MessagingException, TemplateException, IOException;
}
