import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {EmpresaReportesMensualesService} from "../../_services/empresa-reportes-mensuales.service";
import ReporteMensual from "../../_models/ReporteMensual";
import {ToastType} from "../../_enums/ToastType";
import Usuario from "../../_models/Usuario";
import {UsuariosService} from "../../_services/usuarios.service";
import * as datefns from "date-fns";
import {
  BotonEmpresaPersonalComponent
} from "../../_components/botones/boton-empresa-personal/boton-empresa-personal.component";
import {
  BotonEmpresaReportesComponent
} from "../../_components/botones/boton-empresa-reportes/boton-empresa-reportes.component";

@Component({
  selector: 'app-empresa-reportes-mensuales',
  templateUrl: './empresa-reportes-mensuales.component.html',
  styleUrls: ['./empresa-reportes-mensuales.component.css']
})
export class EmpresaReportesMensualesComponent implements OnInit {
  actionInProgress: boolean = false;
  uuid: string;
  private gridApi;
  private gridColumnApi;
  modal: NgbModalRef;
  closeResult: string;
  frameworkComponents: any;
  rowData: ReporteMensual[] = [];
  reporte: ReporteMensual;
  fechaReporte: Date;
  fechaReporteLocal: string;
  mes: string;
  ano: string;
  usuarioActual: Usuario;
  reportaUniformes: boolean = false;
  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Mes', sortable: true, filter: true, valueGetter: function (params) {
      let updatedDate = datefns.subMonths(new Date(params.data.fechaCreacion), 1);
      return updatedDate.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'}).split(" ")[3].toUpperCase()
    }},
    {headerName: 'Año', sortable: true, filter: true, valueGetter: function (params) {
        let updatedDate = datefns.subMonths(new Date(params.data.fechaCreacion), 1);
        return updatedDate.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'}).split(" ")[5]
      }},
    {headerName: 'Fecha creacion', field: 'fechaCreacion', sortable: true, filter: true },
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];

  @ViewChild('mostrarDetallesReporteModal') mostrarDetallesReporteModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaReporteMensualService: EmpresaReportesMensualesService,
              private usuarioService: UsuariosService
              ) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaReportesComponent
    }

    this.usuarioService.obtenerUsuarioActual().subscribe((data: Usuario) => {
      this.usuarioActual = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el usuario actual. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaReporteMensualService.obtenerReportes().subscribe((data: ReporteMensual[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los reportes`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData)
  }

  mostrarModalCrear(modal) {

    this.empresaReporteMensualService.precargarReporte().subscribe((data: ReporteMensual) => {
      this.reporte = data;
      this.fechaReporte = datefns.subMonths(new Date(), 1);
      this.fechaReporteLocal = this.fechaReporte.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'});
      let fechaActualSplice = this.fechaReporteLocal.split(" ");
      this.mes = fechaActualSplice[3].toUpperCase()
      this.ano = fechaActualSplice[5]
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido precargar el reporte`,
        ToastType.ERROR
      );
    })

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarReporteMensual() {
    this.actionInProgress = true;
    let reporte: ReporteMensual = new ReporteMensual();
    reporte.reportaUniformes = false;
    this.empresaReporteMensualService.guardarReporte(reporte).subscribe((data: ReporteMensual) => {
      this.actionInProgress = false;
      this.toastService.showGenericToast(
        "Listo",
        `Se ha enviado el reporte mensual`,
        ToastType.SUCCESS
      )
      window.location.reload();
    }, (error) => {
      this.actionInProgress = false;
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido enviar el reporte. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalDetalles(data) {
    let reporteUuid = data.uuid;

    this.empresaReporteMensualService.obtenerReportePorUuid(reporteUuid).subscribe((data: ReporteMensual) => {
      this.reporte = data;
      this.fechaReporte = datefns.subMonths(new Date(this.reporte.fechaCreacion), 1);
      this.fechaReporteLocal = this.fechaReporte.toLocaleDateString('es-ES', {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'});
      let fechaActualSplice = this.fechaReporteLocal.split(" ");
      this.mes = fechaActualSplice[3].toUpperCase()
      this.ano = fechaActualSplice[5]
      this.modal = this.modalService.open(this.mostrarDetallesReporteModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el equipo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  private getDismissReason(reason: any): string {
    if (reason == ModalDismissReasons.ESC) {
      return `by pressing ESC`;
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return `by clicking on a backdrop`;
    } else {
      return `with ${reason}`;
    }
  }

}
