import {Component, OnInit, ViewChild} from '@angular/core';
import {ReporteoService} from "../../_services/reporteo.service";
import {ToastService} from "../../_services/toast.service";
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {ToastType} from "../../_enums/ToastType";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import ReporteArgos from "../../_models/ReporteArgos";

@Component({
  selector: 'app-reporteo',
  templateUrl: './reporteo.component.html',
  styleUrls: ['./reporteo.component.css']
})
export class ReporteoComponent implements OnInit {

  faDownload = faDownload;
  modal: NgbModalRef;
  private gridApi;
  private gridColumnApi;
  frameworkComponents: any;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true, resizable: true },
    {headerName: 'Tipo', field: 'tipo', sortable: true, filter: true, resizable: true },
    {headerName: 'Status', field: 'status', sortable: true, filter: true, resizable: true },
    {headerName: 'Fecha inicio', field: 'fechaCreacion', sortable: true, filter: true, resizable: true },
    {headerName: 'Ultima actualizacion', field: 'fechaActualizacion', sortable: true, filter: true, resizable: true },
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', resizable: true, cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];

  rowData = [];
  reporte: ReporteArgos;
  fechaDeHoy = new Date().toISOString().split('T')[0];
  tipoReporte: string = undefined;

  @ViewChild('mostrarModalCrearNuevoReporte') mostrarModalCrearNuevoReporte;
  @ViewChild('modalReportePorUuid') modalReportePorUuid;

  constructor(private reporteoService: ReporteoService, private toastService: ToastService,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.reporteoService.obtenerReportesArgos().subscribe((data: ReporteArgos[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los reportes. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  verDetalles() {

  }

  eliminar() {

  }

  mostrarModalDetalles(rowData) {
    let uuid = rowData.uuid;

    this.reporteoService.obtenerReporteArgosPorUuid(uuid).subscribe((data: ReporteArgos) => {
      this.reporte = data;
      this.modal = this.modalService.open(this.modalReportePorUuid, {size: "xl", backdrop: "static"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cambiarTipoReporte(event) {
    this.tipoReporte = event.value
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalNuevoReporte() {
    this.modal = this.modalService.open(this.mostrarModalCrearNuevoReporte, {size: "xl", backdrop: "static"})
  }

  descargarReporte() {
    this.reporteoService.descargarReporteArgosPorUuid(this.reporte.uuid).subscribe((data: ReporteArgos) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha descargado el reporte con exito.`,
        ToastType.SUCCESS
      );

      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "reporte.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  programarReporte() {
    if(this.tipoReporte === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha seleccionado el tipo de reporte`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos programando el reporte`,
      ToastType.INFO
    );

    let reporte = new ReporteArgos();
    reporte.tipo = this.tipoReporte;

    this.reporteoService.programarReporteArgos(reporte).subscribe((data: ReporteArgos) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha programado el reporte con exito`,
        ToastType.SUCCESS
      );
      window.location.reload()
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido progrmar el reporte. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }
}
