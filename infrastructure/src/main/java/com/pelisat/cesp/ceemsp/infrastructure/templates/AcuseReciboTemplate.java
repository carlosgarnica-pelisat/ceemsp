package com.pelisat.cesp.ceemsp.infrastructure.templates;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;

public class AcuseReciboTemplate extends PdfTemplate {
    @Override
    public File generarReporte(String template) throws Exception {
        String filename = "output" + RandomStringUtils.randomNumeric(12);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".html"))) {
            bw.write(template);
        }

        try(OutputStream os = new FileOutputStream(filename + ".pdf")) {
            PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
            pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.withHtmlContent(template, "");
            pdfRendererBuilder.toStream(os).run();

            return new File(filename + ".pdf");
        } catch (Exception ex) {
            throw new Exception();
        }
    }
}
