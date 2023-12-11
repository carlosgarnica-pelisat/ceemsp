import { Component, OnInit } from '@angular/core';
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {ReporteoService} from "../../_services/reporteo.service";
import {ToastService} from "../../_services/toast.service";
import {ToastType} from "../../_enums/ToastType";

@Component({
  selector: 'app-reporteo',
  templateUrl: './reporteo.component.html',
  styleUrls: ['./reporteo.component.css']
})
export class ReporteoComponent implements OnInit {

  faDownload = faDownload;

  constructor(private reporteoService: ReporteoService, private toastService: ToastService) { }

  ngOnInit(): void {
  }

  descargarReporteAcuerdos() {
    this.reporteoService.generarReporteAcuerdos().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido mostrar el reporte de acuerdos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarReportePersonal() {
    this.reporteoService.generarReportePersonal().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte de escrituras. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarReporteCanes() {
    this.reporteoService.generarReporteCanes().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte de escrituras. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarReporteVehiculos() {
    this.reporteoService.generarReporteVehiculos().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte de escrituras. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarReporteClientes() {
    this.reporteoService.generarReporteClientes().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte de escrituras. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarReporteArmas() {
    this.reporteoService.generarReporteArmas().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte de escrituras. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarReporteLicencias() {
    this.reporteoService.generarReporteLicenciasColectivas().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido mostrar el reporte de acuerdos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
