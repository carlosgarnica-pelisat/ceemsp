package com.pelisat.cesp.ceemsp.infrastructure.templates;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;

public class IncidenciaProcedenteTemplate extends PdfTemplate {
    @Override
    public File generarReporte(String template) throws Exception {

        String fileName = "output" + RandomStringUtils.randomNumeric(12);

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName + ".html"))) {
            bufferedWriter.write(template);
        } catch (Exception ex) {

        }

        try(OutputStream os = new FileOutputStream(fileName + ".pdf")) {
            PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
            pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.withHtmlContent(template, "");
            pdfRendererBuilder.toStream(os).run();

            return new File(fileName + ".pdf");
        } catch(Exception ex) {
            throw new Exception();
        }
    }
}
