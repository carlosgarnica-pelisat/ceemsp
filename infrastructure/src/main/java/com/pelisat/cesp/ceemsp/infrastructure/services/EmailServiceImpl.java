package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import com.pelisat.cesp.ceemsp.database.type.NotificacionInternalEmailEnum;
import com.pelisat.cesp.ceemsp.infrastructure.templates.AcuseReciboTemplate;
import com.pelisat.cesp.ceemsp.infrastructure.templates.IncidenciaProcedenteTemplate;
import com.pelisat.cesp.ceemsp.infrastructure.utils.MailTemplateGenerator;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl<T extends CommonModel> implements EmailService {
    private final Configuration configuration;
    private final JavaMailSender javaMailSender;
    private final MailTemplateGenerator mailTemplateGenerator;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, Configuration configuration, MailTemplateGenerator mailTemplateGenerator) {
        this.javaMailSender = javaMailSender;
        this.configuration = configuration;
        this.mailTemplateGenerator = mailTemplateGenerator;
    }

    public void sendEmail(NotificacionEmailEnum notificacionEmailEnum, String to, Map map) throws MessagingException, TemplateException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(notificacionEmailEnum.getMotivo());
        String[] recipients = Arrays.stream(to.trim().split(",")).map(String::trim).toArray(String[]::new);
        helper.setTo(recipients);
        helper.setCc("argos.cesp@jalisco.gob.mx");
        helper.setFrom("sistemas.cesp@jalisco.gob.mx");
        String emailContent = getEmailContent(notificacionEmailEnum, map);

        if(notificacionEmailEnum == NotificacionEmailEnum.ACUSE_RECIBO_INCIDENCIA) {
            try {
                IncidenciaProcedenteTemplate incidenciaProcedenteTemplate = new IncidenciaProcedenteTemplate();
                File pdfOutput = incidenciaProcedenteTemplate.generarReporte(emailContent);
                helper.addAttachment("acuse-recibo", pdfOutput);
            } catch(Exception ex) {
                throw new RuntimeException();
            }
        } else if(notificacionEmailEnum == NotificacionEmailEnum.ACUSE_INFORME_MENSUAL) {
            try {
                AcuseReciboTemplate acuseReciboTemplate = new AcuseReciboTemplate();
                File pdfOutput = acuseReciboTemplate.generarReporte(emailContent);
                helper.addAttachment("informe-mensual", pdfOutput);
            } catch(Exception ex) {
                throw new RuntimeException();
            }
        }

        helper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendInternalMail(NotificacionInternalEmailEnum notificacionInternalEmailEnum, String to, List<Map> map) throws MessagingException, TemplateException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(notificacionInternalEmailEnum.getMotivo());
        helper.setTo(to);
        helper.setFrom("sistemas.cesp@jalisco.gob.mx");
        String emailContent = getInternalEmailContent(notificacionInternalEmailEnum, map);
        helper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }

    private String getInternalEmailContent(NotificacionInternalEmailEnum notificacionInternalEmailEnum, List<Map> map) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("elementos", map);
        configuration.getTemplate(notificacionInternalEmailEnum.getPlantilla()).process(newMap, stringWriter);
        return stringWriter.getBuffer().toString();
    }

    private String getEmailContent(NotificacionEmailEnum notificacionEmailEnum, Map<String, Object> map) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = mailTemplateGenerator.generarPorPlantilla(notificacionEmailEnum, map);
        configuration.getTemplate(notificacionEmailEnum.getPlantilla()).process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }
}
