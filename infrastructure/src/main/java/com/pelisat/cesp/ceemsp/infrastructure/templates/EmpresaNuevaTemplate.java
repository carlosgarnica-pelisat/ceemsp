package com.pelisat.cesp.ceemsp.infrastructure.templates;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;

public class EmpresaNuevaTemplate {

    public String getMailTemplate() {
        return
                "<html>" +
                "<head>" +
                "<style>" +
                ".row { float: left; width: 100%; }" +
                ".col-25 { float: left; width: 25%; }" +
                ".col-75 { float: left; width: 75%; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"row\">"+
                "<div class=\"col-25\">"+
                "<img src=\"https://pelisat.com.mx/logo-ajustado.png\" style=\"width: 100%;\">"+
                "</div>"+
                "<div class=\"col-75\">"+
                "<h2>Creacion de nueva empresa</h2>"+
                "</div>"+
                "</div>"+
                "</body>" +
                "</html>"
                ;
    }
}
