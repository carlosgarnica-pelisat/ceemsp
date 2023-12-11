import {Component, OnInit, ViewChild} from '@angular/core';
import Visita from "../../_models/Visita";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {ToastType} from "../../_enums/ToastType";
import {EmpresaVisitasService} from "../../_services/empresa-visitas.service";
import {faCheck, faDownload} from "@fortawesome/free-solid-svg-icons";
import {
  BotonEmpresaVisitasComponent
} from "../../_components/botones/boton-empresa-visitas/boton-empresa-visitas.component";
import {ReporteoService} from "../../_services/reporteo.service";

@Component({
  selector: 'app-empresa-visitas',
  templateUrl: './empresa-visitas.component.html',
  styleUrls: ['./empresa-visitas.component.css']
})
export class EmpresaVisitasComponent implements OnInit {
  private gridApi;
  private gridColumnApi;
  pestanaActual: string = "DETALLES";
  rowData: Visita[] = [];
  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true,  resizable: true },
    {headerName: 'Tipo', field: 'tipoVisita', sortable: true, filter: true, resizable: true },
    {headerName: 'Num. Orden', field: 'numeroOrden', sortable: true, filter: true,  resizable: true},
    {headerName: 'Nombre empresa', field: 'nombreComercial', sortable: true, filter: true,  pinned: 'left', resizable: true, width: 400, minWidth: 250, maxWidth: 450},
    {headerName: 'Domicilio', sortable: true, filter: true,  resizable: true, valueGetter: function (params) { return params.data.domicilio1 + " " + params.data.numeroExterior}},
    {headerName: 'Fecha de visita', field: 'fechaVisita', sortable: true, filter: true, resizable: true, width: 150, minWidth: 150, maxWidth: 200},
    {headerName: 'Fecha de termino', field: 'fechaTermino', sortable: true, filter: true, resizable: true, width: 150, minWidth: 150, maxWidth: 200},
    {headerName: 'Opciones', cellRenderer: 'empresaVisitaButtonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];

  uuid: string;
  frameworkComponents: any;
  modal: NgbModalRef;
  visita: Visita;
  faDownload = faDownload;

  @ViewChild('mostrarDetallesVisitaModal') mostrarDetallesVisitaModal;

  constructor(private modalService: NgbModal, private toastService: ToastService,
              private route: ActivatedRoute, private empresaVisitaService: EmpresaVisitasService,
              private reporteoService: ReporteoService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      empresaVisitaButtonRenderer: BotonEmpresaVisitasComponent
    }

    this.empresaVisitaService.obtenerVisitas().subscribe((data: Visita[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las visitas`,
        ToastType.ERROR
      );
    })
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }


  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData);
  }

  mostrarModalDetalles(rowData) {
    let visitaUuid = rowData.uuid;
    this.modal = this.modalService.open(this.mostrarDetallesVisitaModal, {ariaLabelledBy: "modal-basic-title", size: 'xl'});

    this.empresaVisitaService.obtenerVisitaEmpresaPorUuid(visitaUuid).subscribe((data: Visita) => {
      this.visita = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion del vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  descargarArchivo(uuid) {
    this.empresaVisitaService.obtenerVisitaArchivoPorUuid(this.visita.uuid, uuid).subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "archivo";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el archivo de la visita`,
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  generarReporteExcel() {
    this.reporteoService.generarReporteVisitas(this.uuid).subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte en excel. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  exportGridData(format) {
    switch(format) {
      case "CSV":
        this.gridApi.exportDataAsCsv();
        break;
      case "PDF":
        this.toastService.showGenericToast(
          "Bajo desarrollo",
          "Actualmente estamos desarrollando esta funcionalidad",
          ToastType.INFO
        )
        break;
      default:
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "No podemos exportar en dicho formato",
          ToastType.WARNING
        )
        break;
    }
  }

}
