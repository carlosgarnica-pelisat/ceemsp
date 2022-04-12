import {Component, OnInit} from '@angular/core';
import {ReporteoService} from "../../_services/reporteo.service";
import {ToastService} from "../../_services/toast.service";
import {faDownload} from "@fortawesome/free-solid-svg-icons";
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

  descargarReporteListadoNominal() {
    this.reporteoService.generarReporteListadoNominal().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido mostrar el listado nominal. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarReportePadronEmpresas() {
    this.reporteoService.generarReportePadronEmpresas().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido mostrar el listado nominal. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
