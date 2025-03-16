package com.pelisat.cesp.ceemsp.infrastructure.services;

import net.glxn.qrgen.javase.QRCode;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCodeServiceImpl implements QRCodeService {
    public String generarQRAcuseBase64(String firma) throws IOException {
        byte[] stream = QRCode
                .from(firma)
                .withSize(150, 150)
                .stream()
                .toByteArray();

        byte[] streamBase64 = Base64.encodeBase64(stream);
        String imgDataBase64 = new String(streamBase64);
        return "data:image/png;base64," + imgDataBase64;
    }
}
