package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoCadenaOriginalEnum;
import com.pelisat.cesp.ceemsp.infrastructure.templates.EmpresaNuevaTemplate;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class NotificacionEmailServiceImpl implements NotificacionEmailService {

    private final CadenaOriginalService cadenaOriginalService;
    private final Environment environment;
    private final JavaMailSender javaMailSender;
    private final Configuration configuration;
    private final Logger logger = LoggerFactory.getLogger(NotificacionEmailService.class);

    @Autowired
    public NotificacionEmailServiceImpl(CadenaOriginalService cadenaOriginalService, Environment environment, JavaMailSender javaMailSender,
                                        Configuration configuration) {
        this.cadenaOriginalService = cadenaOriginalService;
        this.environment = environment;
        this.javaMailSender = javaMailSender;
        this.configuration = configuration;
    }

    @Override
    public void enviarEmail(NotificacionEmailEnum tipoEmail, EmpresaDto empresaDto, UsuarioDto usuarioEmisor, UsuarioDto usuarioReceptor) throws MessagingException, TemplateException, IOException {
        logger.info("Enviando correo electronico del tipo [{}]", tipoEmail.getCodigo());

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailMessage, true);

        EmpresaNuevaTemplate empresaNuevaTemplate = new EmpresaNuevaTemplate();

        mimeMessageHelper.setText(getEmailContent(), true );
        mimeMessageHelper.setFrom("sistemas.cesp@jalisco.gob.mx");
        mimeMessageHelper.setTo(usuarioReceptor.getEmail());
        mimeMessageHelper.setSubject(tipoEmail.getMotivo());

        javaMailSender.send(mailMessage);
    }

    @Override
    public void enviarEmailNotificacion(NotificacionEmailEnum tipoEmail) {
        logger.info("Enviando correo de notificacion ");
    }

    private Session obtenerSesion() {
        return Session.getInstance(obtenerPropiedadesDelServidor(), new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(environment.getProperty("email.outgoing.username"), environment.getProperty("email.outgoing.password"));
            }
        });
    }
    
    private Properties obtenerPropiedadesDelServidor() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", environment.getProperty("email.outgoing.host"));
        properties.put("mail.smtp.port", environment.getProperty("email.outgoing.port"));
        return properties;
    }

    private String getEmailContent() throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        //model.put("user", );
        configuration.getTemplate("notificacion.ftlh").process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }
}
