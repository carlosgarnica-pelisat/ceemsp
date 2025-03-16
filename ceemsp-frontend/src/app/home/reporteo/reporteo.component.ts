import {Component, OnInit, ViewChild} from '@angular/core';
import {ReporteoService} from "../../_services/reporteo.service";
import {ToastService} from "../../_services/toast.service";
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {ToastType} from "../../_enums/ToastType";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import ReporteArgos from "../../_models/ReporteArgos";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Usuario from "../../_models/Usuario";
import {UsuariosService} from "../../_services/usuarios.service";

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
  requiereFecha: boolean = false;
  fechaPersonalizada: boolean = false;
  reporteForm: FormGroup;

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
  fechaDeHoy = new Date().toISOString()?.split('T')[0];
  tipo: string = undefined;
  usuarioActual: Usuario;

  @ViewChild('mostrarModalCrearNuevoReporte') mostrarModalCrearNuevoReporte;
  @ViewChild('modalReportePorUuid') modalReportePorUuid;
  @ViewChild('eliminarReporteModal') eliminarReporteModal;

  constructor(private reporteoService: ReporteoService, private toastService: ToastService,
              private modalService: NgbModal, private fb: FormBuilder, private usuarioService: UsuariosService) { }

  ngOnInit(): void {
    this.usuarioService.obtenerUsuarioActual().subscribe((data: Usuario) => {
      this.usuarioActual = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido obtener el usuario actual. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.reporteForm = this.fb.group(
      {
        tipo: ['', [Validators.required]],
        fechaInicio: ['', []],
        fechaFin: ['', []]
      }
    )

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
    this.tipo = event.value

    if(this.tipo === 'LISTADO_NOMINAL' || this.tipo === 'PADRON_ESTATAL' || this.tipo === 'INTERCAMBIO_INFORMACION') {
      this.requiereFecha = false
    } else {
      this.requiereFecha = true
    }
  }

  cambiarFechaPersonalizada() {
    if(this.fechaPersonalizada) {
      this.reporteForm.controls['fechaInicio'].setValidators([Validators.required])
      this.reporteForm.controls['fechaInicio'].updateValueAndValidity();
      this.reporteForm.controls['fechaFin'].setValidators([Validators.required])
      this.reporteForm.controls['fechaFin'].updateValueAndValidity();
    } else {
      this.reporteForm.controls['fechaInicio'].setValidators([])
      this.reporteForm.controls['fechaInicio'].updateValueAndValidity();
      this.reporteForm.controls['fechaFin'].setValidators([])
      this.reporteForm.controls['fechaFin'].updateValueAndValidity();
    }
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

  mostrarEliminarReporteModal() {
    this.modal = this.modalService.open(this.eliminarReporteModal, {size: "lg", backdrop: "static"})
  }

  eliminarReporte() {
    this.reporteoService.eliminarReporte(this.reporte?.uuid).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado el reporte con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo eliminar el reporte`,
        ToastType.ERROR
      )
    })
  }

  programarReporte(form) {
    console.log(form.value);
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay algunos campos invalidos. Favor de verificar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos programando el reporte`,
      ToastType.INFO
    );

    let reporte: ReporteArgos = form.value;

    if(reporte.fechaInicio !== undefined && reporte.fechaFin !== undefined) {
      let fechaInicio = new Date(reporte.fechaInicio);
      let fechaFin = new Date(reporte.fechaFin);
      if(fechaInicio > fechaFin) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La fecha de inicio es mayor que la del final",
          ToastType.WARNING
        )
        return;
      }
    }

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
