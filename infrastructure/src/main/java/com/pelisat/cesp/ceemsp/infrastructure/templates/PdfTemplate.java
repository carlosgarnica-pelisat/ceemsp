package com.pelisat.cesp.ceemsp.infrastructure.templates;

import java.io.File;

public abstract class PdfTemplate {
    public abstract File generarReporte(String template) throws Exception;
}
