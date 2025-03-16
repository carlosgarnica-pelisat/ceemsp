package com.pelisat.cesp.ceemsp.infrastructure.services;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface QRCodeService {
    String generarQRAcuseBase64(String firma) throws IOException;
}
