package com.pelisat.cesp.ceemsp.restceemsp.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
public class ReporteoServiceImpl implements ReporteoService {
    private final Logger logger = LoggerFactory.getLogger(ReporteoService.class);

    @Autowired
    public ReporteoServiceImpl() {}


    @Override
    public File generarReporteListadoNominal() throws Exception {
        Workbook workbook = new HSSFWorkbook();
        OutputStream outputStream = new FileOutputStream("test.xls");

        Sheet sheet = workbook.createSheet("Listado nominal");

        // Generando encabezado del archivo
        Row encabezadosUnidosRow = sheet.createRow(0);
        Cell personalOperativoCell = encabezadosUnidosRow.createCell(1);
        personalOperativoCell.setCellValue("Personal Operativo");

        Cell empresaDeSeguridadPrivadaCell = encabezadosUnidosRow.createCell(9);
        empresaDeSeguridadPrivadaCell.setCellValue("Empresa de Seguridad Privada");

        // Sigue el encabezado del archivo
        Row encabezadoReporteRow = sheet.createRow(1);
        Cell noConsecutivoEncabezadoCell = encabezadoReporteRow.createCell(0);
        Cell cuipEncabezadoCell = encabezadoReporteRow.createCell(1);
        Cell noRegistroElementoEncabezadoCell = encabezadoReporteRow.createCell(2);
        Cell rfcEncabezadoCell = encabezadoReporteRow.createCell(3);
        Cell curpEncabezadoCell = encabezadoReporteRow.createCell(4);
        Cell apellidoPaternoEncabezadoCell = encabezadoReporteRow.createCell(5);
        Cell apellidoMaternoEncabezadoCell = encabezadoReporteRow.createCell(6);
        Cell nombresEncabezadoCell = encabezadoReporteRow.createCell(7);
        Cell fechaIngresoCell = encabezadoReporteRow.createCell(8);

        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(9);
        Cell noRegistroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(10);
        Cell modalidadesAutorizadasEncabezadoCell = encabezadoReporteRow.createCell(11);

        noConsecutivoEncabezadoCell.setCellValue("No. Consecutivo");
        cuipEncabezadoCell.setCellValue("CUIP");
        noRegistroElementoEncabezadoCell.setCellValue("No. Registro del Elemento en el Estado");
        rfcEncabezadoCell.setCellValue("RFC");
        curpEncabezadoCell.setCellValue("CURP");
        apellidoPaternoEncabezadoCell.setCellValue("Apellido Paterno");
        apellidoMaternoEncabezadoCell.setCellValue("Apellido Materno");
        nombresEncabezadoCell.setCellValue("Nombres");
        fechaIngresoCell.setCellValue("Fecha de Ingreso");

        razonSocialEmpresaEncabezadoCell.setCellValue("Razon Social");
        noRegistroEmpresaEncabezadoCell.setCellValue("No. Registro de la Empresa Estatal");
        modalidadesAutorizadasEncabezadoCell.setCellValue("Modalidades autorizadas");

        // Mergeando las celdas para generar los encabezados grandes
        sheet.addMergedRegion(CellRangeAddress.valueOf("B1:I1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J1:L1"));

        workbook.write(outputStream);

        return new File("test.xls");
    }

    @Override
    public File generarReportePadronEmpresas() throws Exception {
        return null;
    }
}
